package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.service.UserServicePort;
import com.people.hub.leavemanagement.dto.LeaveBalanceDto;
import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import com.people.hub.leavemanagement.model.LeaveBalance;
import com.people.hub.leavemanagement.model.LeaveType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LeaveAllocationService {
    private static final int BATCH_SIZE = 500;
    private final LeaveBalanceService balanceService;
    private final LeaveTransactionService transactionService;
    private final UserServicePort userServicePort;
    private final LeaveTypeService leaveTypeService;

    public RestApiResponse allocateLeave(LeaveBalanceDto leaveBalanceDto){
        transactionService.recordAllocation(
                leaveBalanceDto.getEmployeeId(),
                leaveBalanceDto.getLeaveTypeId(),
                leaveBalanceDto.getAllocatedLeaves(),
                "Allocation"
        );
        return RestApiResponse.success(balanceService.createLeaveBalance(leaveBalanceDto));
    }

    public RestApiResponse allocateLeaveBulk(List<LeaveBalanceDto> leaveBalanceDtoList){
        return balanceService.bulkCreateLeaveBalance(leaveBalanceDtoList);
    }

    public RestApiResponse allocateAnnualLeaves(){
        List<Long> employeeIds = userServicePort.getAllUserIds();

        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypesList();
        List<LeaveBalanceDto> batch = new ArrayList<>(BATCH_SIZE);

        int batchNumber = 0;
        log.info("Started annual leave allocation");
        for(Long employeeId: employeeIds) {
            for(LeaveType leaveType: leaveTypes) {
                LeaveBalanceDto leaveBalanceDto = new LeaveBalanceDto();
                leaveBalanceDto.setEmployeeId(employeeId);
                leaveBalanceDto.setLeaveTypeId(leaveType.getId());
                leaveBalanceDto.setAllocatedLeaves(leaveType.getAnnualAllocation());
                batch.add(leaveBalanceDto);
                if(batch.size() >= BATCH_SIZE) {
                    log.info("Processing batch: {}, size={}", batchNumber, batch.size());
                    RestApiResponse response = allocateLeaveBulk(batch);
                    if(!response.isSuccess()) {
                        log.error("Batch: {}, size: {} failed", batchNumber, batch.size());
                    }
                    batch = new ArrayList<>(BATCH_SIZE);
                    batchNumber++;
                }
            }
        }
        if (!batch.isEmpty()) {
            log.info("Processing batch: {}, size={}", batchNumber, batch.size());
            RestApiResponse response = allocateLeaveBulk(batch);
            if (!response.isSuccess()) {
                log.error("Final batch: {}, size={} failed", batchNumber, batch.size());
            }
        }
        log.info("Annual leave allocation completed");
        return RestApiResponse.success("Bulk insert successful");
    }

    public RestApiResponse allocateAnnualLeaves(Long employeeId){
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypesList();
        List<LeaveBalanceDto> batch = new ArrayList<>();

        log.info("Started annual leave allocation for employee: {}", employeeId);
        for(LeaveType leaveType: leaveTypes) {
            LeaveBalanceDto leaveBalanceDto = new LeaveBalanceDto();
            leaveBalanceDto.setEmployeeId(employeeId);
            leaveBalanceDto.setLeaveTypeId(leaveType.getId());
            leaveBalanceDto.setAllocatedLeaves(leaveType.getAnnualAllocation());
            batch.add(leaveBalanceDto);
        }
        allocateLeaveBulk(batch);
        log.info("Annual leave allocation completed");
        return RestApiResponse.success("Bulk insert successful");
    }

    public RestApiResponse adjustLeaveBalance(
            Long leaveBalanceId,
            Float days,
            String remarks){
        LeaveBalance leaveBalance = balanceService.getLeaveBalanceById(leaveBalanceId);
        transactionService.recordAdjustmentAllocation(
                leaveBalance.getEmployeeId(),
                leaveBalance.getLeaveTypeId(),
                days,
                remarks);
        balanceService.updateLeaveBalance(leaveBalanceId, days);
        return RestApiResponse.success("Adjusted allocated leaves successfully");
    }

    public RestApiResponse adjustCarryForward(
            Long leaveBalanceId,
            Float days,
            String remarks){
        LeaveBalance leaveBalance = balanceService.getLeaveBalanceById(leaveBalanceId);
        transactionService.recordCarryForward(
                leaveBalance.getEmployeeId(),
                leaveBalance.getLeaveTypeId(),
                days,
                remarks);
        balanceService.updateCarryForward(leaveBalanceId, days);
        return RestApiResponse.success("Adjusted allocated leaves successfully");
    }

    public RestApiResponse getAllocationHistory(Long employeeId){
        return transactionService.getEmployeeTransactions(employeeId);
    }

    public RestApiResponse getAllocationHistory(
            Long employeeId,
            Long leaveTypeId){
        return transactionService.getHistoryByEmployeeIdLeaveTypeIdTransactionType(
                employeeId, leaveTypeId, LeaveTransactionType.ALLOCATION);
    }
}
