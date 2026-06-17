package com.people.hub.attendancemanagement.dto;

import com.company.attendance.enums.PunchDirection;
import com.company.attendance.enums.VerifyMode;
import java.time.LocalDateTime;

/** Read-only projection of AttendancePunch exposed via the application API.
 * Deliberately excludes rawPayloadLine/deviceSequence — internal debugging
 * fields that the frontend/HR dashboard has no use for. */
public class PunchResponse {

    private final Long id;
    private final Long employeeId;
    private final LocalDateTime punchTimestamp;
    private final PunchDirection direction;
    private final VerifyMode verifyMode;
    private final String deviceSerial;
    private final boolean resolved;

    public PunchResponse(Long id, Long employeeId, LocalDateTime punchTimestamp,
                          PunchDirection direction, VerifyMode verifyMode,
                          String deviceSerial, boolean resolved) {
        this.id = id;
        this.employeeId = employeeId;
        this.punchTimestamp = punchTimestamp;
        this.direction = direction;
        this.verifyMode = verifyMode;
        this.deviceSerial = deviceSerial;
        this.resolved = resolved;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDateTime getPunchTimestamp() { return punchTimestamp; }
    public PunchDirection getDirection() { return direction; }
    public VerifyMode getVerifyMode() { return verifyMode; }
    public String getDeviceSerial() { return deviceSerial; }
    public boolean isResolved() { return resolved; }
}
