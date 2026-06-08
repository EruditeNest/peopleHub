package com.people.hub.leavemanagement.dto;

import lombok.Data;

@Data
public class LeaveBalanceDto {

    private Long employeeId;

    private Long leaveTypeId;

    private String financialYear;

    private Float allocatedLeaves;

    private Float usedLeaves;

    private Float pendingLeaves;

    private Float availableLeaves;

    private Float carryForwardLeaves;
}
