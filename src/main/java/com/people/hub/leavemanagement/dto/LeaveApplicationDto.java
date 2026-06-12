package com.people.hub.leavemanagement.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LeaveApplicationDto {

    private Long leaveTypeId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal totalDays;

    private Long managerId;

    private Long hrId;

    private String reason;

    private String managerRemarks;

    private String hrRemarks;

    private LocalDateTime appliedAt;
}
