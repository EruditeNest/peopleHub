package com.people.hub.leavemanagement.model;

import com.people.hub.leavemanagement.enums.LeaveTypeName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveTypeName leaveType;

    private BigDecimal annualAllocation;

    private Boolean carryForwardAllowed;

    private Integer maxCarryForward;

    private Boolean active;
}
