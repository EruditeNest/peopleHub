package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.LeaveTransactionSource;
import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
public class LeaveTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private Long leaveApplicationId;

    @Enumerated(EnumType.STRING)
    private LeaveTransactionSource leaveTransactionSource;

    @Enumerated(EnumType.STRING)
    private LeaveTransactionType leaveTransactionType;

    private Float days;

    private String remarks;

    private LocalDateTime transactionDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
