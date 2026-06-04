package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal totalDays;

    private LeaveStatus status;

    private Long managerId;

    private Long hrId;

    @Column(length = 1000)
    private String reason;

    @Column(length = 1000)
    private String managerRemarks;

    @Column(length = 1000)
    private String hrRemarks;

    private LocalDateTime appliedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;
}
