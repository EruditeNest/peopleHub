package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
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
    private LeaveTransactionType leaveTransactionType;

    @Enumerated(EnumType.STRING)
    private LeaveTransactionType transactionType;

    private BigDecimal days;

    private String remarks;

    private LocalDateTime transactionDate;
}
