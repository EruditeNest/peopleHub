package com.people.hub.attendancemanagement.service;

import com.people.hub.attendancemanagement.dto.ParsedPunchLine;
import com.people.hub.attendancemanagement.enums.PunchDirection;
import com.people.hub.attendancemanagement.enums.VerifyMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses the plain-text ATTLOG body pushed by ZKTeco-family ADMS devices.
 *
 * Format (tab-separated, one record per line):
 *   PIN \t TIMESTAMP \t STATUS \t VERIFY \t WORKCODE [\t RESERVED...]
 *
 * Example line:
 *   23\t2026-06-17 09:02:14\t0\t1\t0
 *
 * STATUS code meaning (varies by firmware, but commonly):
 *   0 = check-in, 1 = check-out, 2 = break-out, 3 = break-in, 4 = OT-in, 5 = OT-out
 * VERIFY code meaning:
 *   1 = fingerprint, 15 = face, 2 = card/RFID (varies by model)
 *
 * IMPORTANT: confirm the exact status/verify code table against YOUR device
 * model's protocol doc before going live — these vary across firmware
 * versions and vendors (ZKTeco vs eSSL differ). Treat the mapping below as a
 * starting point to be calibrated against real device logs.
 */
@Component
public class AdmsAttLogParser {

    private static final Logger log = LoggerFactory.getLogger(AdmsAttLogParser.class);
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<ParsedPunchLine> parse(String body) {
        List<ParsedPunchLine> result = new ArrayList<>();
        if (body == null || body.isBlank()) {
            return result;
        }

        String[] lines = body.split("\\r?\\n");
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                ParsedPunchLine parsed = parseLine(line);
                if (parsed != null) {
                    result.add(parsed);
                }
            } catch (Exception e) {
                // Never let one malformed line abort the whole batch — log and skip.
                log.warn("Skipping unparseable ATTLOG line: '{}' - {}", line, e.getMessage());
            }
        }
        return result;
    }

    private ParsedPunchLine parseLine(String line) {
        String[] parts = line.split("\\t");
        if (parts.length < 2) {
            log.warn("ATTLOG line has too few fields, skipping: '{}'", line);
            return null;
        }

        String pin = parts[0].trim();
        LocalDateTime timestamp;
        try {
            timestamp = LocalDateTime.parse(parts[1].trim(), TS_FORMAT);
        } catch (DateTimeParseException e) {
            log.warn("Unparseable timestamp '{}' in line '{}'", parts[1], line);
            return null;
        }

        String statusCode = parts.length > 2 ? parts[2].trim() : null;
        String verifyCode = parts.length > 3 ? parts[3].trim() : null;

        return new ParsedPunchLine(
                pin,
                timestamp,
                statusCode,
                mapDirection(statusCode),
                mapVerifyMode(verifyCode),
                line
        );
    }

    private PunchDirection mapDirection(String statusCode) {
        if (statusCode == null) {
            return PunchDirection.UNKNOWN;
        }
        switch (statusCode) {
            case "0": return PunchDirection.IN;
            case "1": return PunchDirection.OUT;
            default: return PunchDirection.UNKNOWN; // breaks/OT codes left UNKNOWN deliberately
        }
    }

    private VerifyMode mapVerifyMode(String verifyCode) {
        if (verifyCode == null) {
            return VerifyMode.UNKNOWN;
        }
        switch (verifyCode) {
            case "1": return VerifyMode.FINGERPRINT;
            case "15": return VerifyMode.FACE;
            case "2": return VerifyMode.CARD;
            case "3": return VerifyMode.PASSWORD;
            default: return VerifyMode.UNKNOWN;
        }
    }
}
