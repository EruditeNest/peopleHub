package com.people.hub.attendancemanagement.dto;

import com.people.hub.attendancemanagement.enums.CorrectionType;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateCorrectionRequest {

    private Long employeeId;
    private LocalDate workDate;
    private CorrectionType correctionType;
    private LocalDateTime requestedInTime;
    private LocalDateTime requestedOutTime;
    private Integer requestedWorkedMinutes;
    private String reason;
    private String supportingDocumentRef;

}
