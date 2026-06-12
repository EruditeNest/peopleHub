package com.people.hub.leavemanagement.dto;

import com.people.hub.leavemanagement.enums.LeaveTypeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeaveTypeDto {
    @Enumerated(EnumType.STRING)
    private LeaveTypeName leaveTypeName;

    private float annualAllocation;

    private Boolean carryForwardAllowed;

    private float maxCarryForward;

    private Boolean active;
}
