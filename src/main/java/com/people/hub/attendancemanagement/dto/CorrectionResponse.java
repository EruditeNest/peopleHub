package com.people.hub.attendancemanagement.dto;

import com.people.hub.attendancemanagement.model.AttendanceCorrection;
import com.people.hub.attendancemanagement.enums.CorrectionStatus;
import com.people.hub.attendancemanagement.enums.CorrectionType;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CorrectionResponse {

    private final Long id;
    private final Long employeeId;
    private final LocalDate workDate;
    private final CorrectionType correctionType;
    private final LocalDateTime requestedInTime;
    private final LocalDateTime requestedOutTime;
    private final Integer requestedWorkedMinutes;
    private final String reason;
    private final CorrectionStatus status;
    private final Long requestedByEmployeeId;
    private final LocalDateTime requestedAt;
    private final Long decidedByEmployeeId;
    private final LocalDateTime decidedAt;
    private final String decisionNote;

    public CorrectionResponse(AttendanceCorrection c) {
        this.id = c.getId();
        this.employeeId = c.getEmployeeId();
        this.workDate = c.getWorkDate();
        this.correctionType = c.getCorrectionType();
        this.requestedInTime = c.getRequestedInTime();
        this.requestedOutTime = c.getRequestedOutTime();
        this.requestedWorkedMinutes = c.getRequestedWorkedMinutes();
        this.reason = c.getReason();
        this.status = c.getStatus();
        this.requestedByEmployeeId = c.getRequestedByEmployeeId();
        this.requestedAt = c.getRequestedAt();
        this.decidedByEmployeeId = c.getDecidedByEmployeeId();
        this.decidedAt = c.getDecidedAt();
        this.decisionNote = c.getDecisionNote();
    }

}
