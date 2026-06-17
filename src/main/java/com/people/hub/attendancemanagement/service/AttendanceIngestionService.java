package com.people.hub.attendancemanagement.service;

import com.company.attendance.dto.ParsedPunchLine;
import com.company.attendance.entity.AttendancePunch;
import com.company.attendance.entity.BiometricDevice;
import com.company.attendance.entity.EmployeeDeviceMapping;
import com.company.attendance.exception.UnknownDeviceException;
import com.company.attendance.repository.AttendancePunchRepository;
import com.company.attendance.repository.BiometricDeviceRepository;
import com.company.attendance.repository.EmployeeDeviceMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Owns the business rules around accepting raw device pushes:
 *  - reject pushes from unregistered/inactive devices
 *  - resolve device-local PIN -> internal employee id (best-effort; punch is
 *    still stored even if resolution fails, flagged unresolved for ops review)
 *  - de-duplicate retried pushes (devices resend on any non-OK response)
 *  - publish a domain event per successfully stored NEW punch, so work-hours/
 *    overtime computation can react asynchronously without coupling to this
 *    ingestion path
 */
@Service
public class AttendanceIngestionService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceIngestionService.class);

    private final AttendancePunchRepository punchRepository;
    private final BiometricDeviceRepository deviceRepository;
    private final EmployeeDeviceMappingRepository mappingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AttendanceIngestionService(AttendancePunchRepository punchRepository,
                                       BiometricDeviceRepository deviceRepository,
                                       EmployeeDeviceMappingRepository mappingRepository,
                                       ApplicationEventPublisher eventPublisher) {
        this.punchRepository = punchRepository;
        this.deviceRepository = deviceRepository;
        this.mappingRepository = mappingRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Validates the device is registered+active. Call this first, before
     * attempting to parse/store anything, so an unknown device gets a clean
     * rejection rather than partial processing.
     *
     * @throws UnknownDeviceException if the serial isn't registered or is inactive
     */
    @Transactional(readOnly = true)
    public BiometricDevice validateDevice(String deviceSerial) {
        return deviceRepository.findBySerialNumberAndActiveTrue(deviceSerial)
                .orElseThrow(() -> new UnknownDeviceException(
                        "Rejected push from unregistered or inactive device: " + deviceSerial));
    }

    /**
     * Stores a batch of parsed punch lines for one device push. Each line is
     * handled in its own small transaction-safe insert so that one duplicate/
     * bad line doesn't roll back the whole batch.
     *
     * @return count of lines that were newly inserted (for logging/metrics;
     *         duplicates and unresolved-but-stored lines are not counted as failures)
     */
    public int ingestBatch(BiometricDevice device, List<ParsedPunchLine> lines) {
        // Cache PIN->employeeId resolutions for this batch to avoid N repeated
        // queries when one device pushes many lines for the same employee.
        Map<String, Optional<Long>> resolutionCache = new HashMap<>();
        int newlyInserted = 0;
        int sequenceWithinBatch = 0;

        for (ParsedPunchLine line : lines) {
            sequenceWithinBatch++;
            Long employeeId = resolutionCache.computeIfAbsent(line.getDevicePin(),
                    pin -> resolveEmployeeId(device.getSerialNumber(), pin)).orElse(null);

            boolean wasNew = storeSinglePunch(device, line, employeeId, sequenceWithinBatch);
            if (wasNew) {
                newlyInserted++;
            }
        }
        device.markSeenNow(LocalDateTime.now());
        deviceRepository.save(device);
        return newlyInserted;
    }

    private Optional<Long> resolveEmployeeId(String deviceSerial, String devicePin) {
        return mappingRepository.findByDeviceSerialAndDeviceEmployeePinAndActiveTrue(deviceSerial, devicePin)
                .map(EmployeeDeviceMapping::getEmployeeId);
    }

    /**
     * Inserts one punch idempotently. Relies on the DB unique constraint as
     * the ultimate source of truth for de-duplication (catching the
     * constraint violation) rather than only a SELECT-then-INSERT check,
     * since concurrent pushes/retries can race between the check and insert.
     */
    @Transactional
    protected boolean storeSinglePunch(BiometricDevice device, ParsedPunchLine line,
                                        Long employeeId, int deviceSequence) {
        Optional<AttendancePunch> existing = punchRepository
                .findByDeviceSerialAndDeviceEmployeePinAndPunchTimestampAndDeviceSequence(
                        device.getSerialNumber(), line.getDevicePin(), line.getTimestamp(), deviceSequence);

        if (existing.isPresent()) {
            log.debug("Duplicate punch ignored: device={} pin={} ts={}",
                    device.getSerialNumber(), line.getDevicePin(), line.getTimestamp());
            return false;
        }

        AttendancePunch punch = new AttendancePunch(
                device.getSerialNumber(),
                line.getDevicePin(),
                employeeId,
                line.getTimestamp(),
                deviceSequence,
                line.getDirection(),
                line.getVerifyMode(),
                line.getRawStatusCode(),
                line.getRawLine(),
                LocalDateTime.now(),
                employeeId != null
        );

        try {
            AttendancePunch saved = punchRepository.save(punch);
            if (employeeId == null) {
                log.warn("Stored UNRESOLVED punch: device={} pin={} ts={} - no active employee mapping found",
                        device.getSerialNumber(), line.getDevicePin(), line.getTimestamp());
            }
            eventPublisher.publishEvent(new PunchRecordedEvent(saved.getId(), employeeId, line.getTimestamp()));
            return true;
        } catch (DataIntegrityViolationException e) {
            // Lost a race with a concurrent identical push; treat as duplicate, not an error.
            log.debug("Concurrent duplicate punch caught at DB constraint level, ignoring.");
            return false;
        }
    }

    /** Lightweight domain event; downstream listeners (work-hours calc, overtime
     * calc, live dashboards) subscribe to this rather than depending on this
     * service directly. Kept here for cohesion; move to its own file if it grows. */
    public record PunchRecordedEvent(Long punchId, Long employeeId, LocalDateTime punchTimestamp) {}
}
