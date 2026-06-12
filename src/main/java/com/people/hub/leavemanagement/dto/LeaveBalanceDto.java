package com.people.hub.leavemanagement.dto;

import lombok.Data;

@Data
public class LeaveBalanceDto {

    private Long employeeId;

    private Long leaveTypeId;

    private float allocatedLeaves = 0f;
}
