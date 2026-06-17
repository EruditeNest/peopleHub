package com.people.hub.attendancemanagement.controller;

import com.company.attendance.dto.ParsedPunchLine;
import com.company.attendance.entity.BiometricDevice;
import com.company.attendance.exception.UnknownDeviceException;
import com.company.attendance.service.AdmsAttLogParser;
import com.company.attendance.service.AttendanceIngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Speaks the raw ADMS push protocol used by ZKTeco-family biometric devices.
 *
 * This is deliberately NOT a typical JSON REST controller. The device firmware
 * dictates the request/response shape; we cannot change it. Endpoints:
 *
 *  GET  /iclock/cdata   - device handshake / heartbeat / command poll
 *  POST /iclock/cdata   - actual attendance data push (table=ATTLOG)
 *
 * The device expects plain-text responses, NOT JSON. Returning JSON or the
 * wrong status code here will cause the device to treat the push as failed
 * and retry indefinitely, flooding you with duplicate data.
 *
 * Path is unauthenticated by design (devices generally can't do OAuth/JWT
 * headers), so device identity is verified via SN + the registered-device
 * table, and optionally CommKey if your model supports it. This endpoint
 * MUST be reachable only from trusted network paths in production — see
 * the network notes at the end of this file/response.
 */
@RestController
@RequestMapping("/iclock")
public class AdmsDeviceController {

    private static final Logger log = LoggerFactory.getLogger(AdmsDeviceController.class);

    private final AttendanceIngestionService ingestionService;
    private final AdmsAttLogParser parser;

    public AdmsDeviceController(AttendanceIngestionService ingestionService, AdmsAttLogParser parser) {
        this.ingestionService = ingestionService;
        this.parser = parser;
    }

    /**
     * Device handshake. Sent periodically (heartbeat) and on boot. We don't
     * need to push remote commands back for a basic attendance-only
     * integration, so we just acknowledge and tell it we have no pending
     * commands. Real protocol expects specific plain-text lines, not just "OK".
     */
    @GetMapping(value = "/cdata", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> handshake(
            @RequestParam("SN") String deviceSerial,
            @RequestParam(value = "options", required = false) String options,
            @RequestParam(value = "pushver", required = false) String pushVersion) {

        try {
            BiometricDevice device = ingestionService.validateDevice(deviceSerial);
            log.info("Handshake from device {} ({})", deviceSerial, device.getLabel());
            // Standard ADMS ack: GET OPTION FROM is what real firmware expects here.
            // ServerVersion/PushProtoVer values below are illustrative — confirm
            // against your specific firmware's protocol doc.
            String response = "GET OPTION FROM: " + deviceSerial + "\n"
                    + "Stamp=" + System.currentTimeMillis() + "\n"
                    + "OpStamp=" + System.currentTimeMillis() + "\n"
                    + "ErrorDelay=60\n"
                    + "Delay=30\n"
                    + "TransTimes=00:00;14:00\n"
                    + "TransInterval=1\n"
                    + "TransFlag=1111000000\n"
                    + "Realtime=1\n"
                    + "Encrypt=0";
            return ResponseEntity.ok(response);
        } catch (UnknownDeviceException e) {
            log.warn(e.getMessage());
            // Plain-text non-OK response; unregistered device will keep retrying,
            // which is fine — it surfaces in your logs/metrics until you register it.
            return ResponseEntity.status(403).body("ERROR: UNKNOWN DEVICE");
        }
    }

    /**
     * Actual data push. table=ATTLOG carries attendance punches; devices also
     * push OPERLOG (admin operations) and other tables we ignore here.
     * Body is plain text, one record per line — see AdmsAttLogParser for format.
     */
    @PostMapping(value = "/cdata", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> receiveData(
            @RequestParam("SN") String deviceSerial,
            @RequestParam(value = "table", required = false, defaultValue = "ATTLOG") String table,
            @RequestParam(value = "Stamp", required = false) String stamp,
            @RequestBody(required = false) String body) {

        BiometricDevice device;
        try {
            device = ingestionService.validateDevice(deviceSerial);
        } catch (UnknownDeviceException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(403).body("ERROR: UNKNOWN DEVICE");
        }

        if (!"ATTLOG".equalsIgnoreCase(table)) {
            // We don't process OPERLOG/other tables in this module; ack anyway
            // so the device doesn't get stuck retrying data we'll never use.
            log.debug("Ignoring non-ATTLOG push (table={}) from device {}", table, deviceSerial);
            return ResponseEntity.ok("OK");
        }

        List<ParsedPunchLine> lines = parser.parse(body);
        if (lines.isEmpty()) {
            log.warn("Empty/unparseable ATTLOG body from device {}: '{}'", deviceSerial, body);
            // Still return OK — returning an error here causes infinite resend
            // of a payload we'll never successfully parse.
            return ResponseEntity.ok("OK");
        }

        int newCount = ingestionService.ingestBatch(device, lines);
        log.info("Device {} pushed {} line(s), {} newly stored", deviceSerial, lines.size(), newCount);

        // The device only cares that we returned 200 + "OK" body; it does not
        // need per-line acknowledgement detail.
        return ResponseEntity.ok("OK");
    }
}
