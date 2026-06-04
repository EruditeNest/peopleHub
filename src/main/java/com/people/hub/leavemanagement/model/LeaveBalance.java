package com.people.hub.leavemanagement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "employeeId",
                                "leaveTypeId",
                                "leaveYear"
                        }
                )
        }
)
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private Integer leaveYear;

    private BigDecimal allocatedLeaves;

    private BigDecimal usedLeaves;

    private BigDecimal pendingLeaves;

    private BigDecimal availableLeaves;

    private BigDecimal carryForwardLeaves;
}
