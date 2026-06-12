package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
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

    private Float totalDays;

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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
