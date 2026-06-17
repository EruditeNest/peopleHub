package com.people.hub.attendancemanagement.dto;

import com.people.hub.attendancemanagement.enums.PunchDirection;
import com.people.hub.attendancemanagement.enums.VerifyMode;
import java.time.LocalDateTime;

/**
 * Result of parsing a single line of an ADMS ATTLOG push body.
 * Typical raw line (tab-separated, ZKTeco-family format):
 *   {devicePin}\t{yyyy-MM-dd HH:mm:ss}\t{status}\t{verifyMode}\t{workCode}\t...
 * Field count/order varies by firmware version, so the parser is defensive
 * about trailing/missing columns.
 */
public class ParsedPunchLine {

    private final String devicePin;
    private final LocalDateTime timestamp;
    private final String rawStatusCode;
    private final PunchDirection direction;
    private final VerifyMode verifyMode;
    private final String rawLine;

    public ParsedPunchLine(String devicePin, LocalDateTime timestamp, String rawStatusCode,
                            PunchDirection direction, VerifyMode verifyMode, String rawLine) {
        this.devicePin = devicePin;
        this.timestamp = timestamp;
        this.rawStatusCode = rawStatusCode;
        this.direction = direction;
        this.verifyMode = verifyMode;
        this.rawLine = rawLine;
    }

    public String getDevicePin() { return devicePin; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getRawStatusCode() { return rawStatusCode; }
    public PunchDirection getDirection() { return direction; }
    public VerifyMode getVerifyMode() { return verifyMode; }
    public String getRawLine() { return rawLine; }
}
