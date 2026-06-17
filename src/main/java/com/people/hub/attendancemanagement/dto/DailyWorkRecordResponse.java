package com.people.hub.attendancemanagement.dto;

import com.people.hub.attendancemanagement.model.DailyWorkRecord;
import com.people.hub.attendancemanagement.enums.DailyWorkRecordStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DailyWorkRecordResponse {

    private final Long employeeId;
    private final LocalDate workDate;
    private final LocalDateTime firstPunchIn;
    private final LocalDateTime lastPunchOut;
    private final int totalWorkedMinutes;
    private final int overtimeMinutes;
    private final int punchPairCount;
    private final DailyWorkRecordStatus status;
    private final Long appliedCorrectionId;

    public DailyWorkRecordResponse(DailyWorkRecord r) {
        this.employeeId = r.getEmployeeId();
        this.workDate = r.getWorkDate();
        this.firstPunchIn = r.getFirstPunchIn();
        this.lastPunchOut = r.getLastPunchOut();
        this.totalWorkedMinutes = r.getTotalWorkedMinutes();
        this.overtimeMinutes = r.getOvertimeMinutes();
        this.punchPairCount = r.getPunchPairCount();
        this.status = r.getStatus();
        this.appliedCorrectionId = r.getAppliedCorrectionId();
    }

}
