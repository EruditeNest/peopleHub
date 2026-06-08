package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.BadRequestException;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.leavemanagement.dto.LeaveBalanceDto;
import com.people.hub.leavemanagement.model.LeaveBalance;
import com.people.hub.leavemanagement.repository.LeaveBalanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {
    private final LeaveBalanceRepo leaveBalanceRepo;

    public LeaveBalance createLeaveBalance(LeaveBalanceDto leaveBalanceDto){
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setAllocatedLeaves(leaveBalanceDto.getAllocatedLeaves());
        leaveBalance.setLeaveTypeId(leaveBalanceDto.getLeaveTypeId());
        leaveBalance.setEmployeeId(leaveBalanceDto.getEmployeeId());

        String financialYear = getFinancialYear(LocalDate.now());
        leaveBalance.setFinancialYear(financialYear);

        Float availableLeaves = leaveBalanceDto.getAllocatedLeaves() + leaveBalanceDto.getCarryForwardLeaves();
        leaveBalance.setAvailableLeaves(availableLeaves);

        leaveBalance.setPendingLeaves(0f);
        leaveBalance.setUsedLeaves(0f);
        leaveBalance.setCarryForwardLeaves(leaveBalanceDto.getCarryForwardLeaves());
        return leaveBalanceRepo.save(leaveBalance);
    }

    public LeaveBalance updateLeaveBalance(Long leaveBalanceId, LeaveBalanceDto leaveBalanceDto){
        LeaveBalance leaveBalance = getLeaveBalanceById(leaveBalanceId);
        leaveBalance.setAllocatedLeaves(leaveBalanceDto.getAllocatedLeaves());
        leaveBalance.setLeaveTypeId(leaveBalanceDto.getLeaveTypeId());
        leaveBalance.setEmployeeId(leaveBalanceDto.getEmployeeId());

        String financialYear = getFinancialYear(LocalDate.now());
        leaveBalance.setFinancialYear(financialYear);

        Float availableLeaves = leaveBalanceDto.getAllocatedLeaves() + leaveBalanceDto.getCarryForwardLeaves();
        leaveBalance.setAvailableLeaves(availableLeaves);

        leaveBalance.setPendingLeaves(0f);
        leaveBalance.setUsedLeaves(0f);
        leaveBalance.setCarryForwardLeaves(leaveBalanceDto.getCarryForwardLeaves());
        return leaveBalanceRepo.save(leaveBalance);
    }

    public LeaveBalance getLeaveBalanceById(Long id){
        return leaveBalanceRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("LeaveBalance not found"));
    }

    public LeaveBalance getLeaveBalance(
            Long employeeId,
            Long leaveTypeId){
        String financialYear = getFinancialYear(LocalDate.now());
        return leaveBalanceRepo.findByEmployeeIdAndLeaveTypeIdAndFinancialYear(employeeId, leaveTypeId, financialYear)
                .orElseThrow(() -> new NotFoundException("LeaveBalance not found"));
    }

    public RestApiResponse getEmployeeLeaveBalances(Long employeeId){
        String financialYear = getFinancialYear(LocalDate.now());
        List<LeaveBalance> leaveBalanceList = leaveBalanceRepo.findByEmployeeIdAndFinancialYear(employeeId, financialYear);
        return RestApiResponse.success(leaveBalanceList);
    }

    public Float allocatedLeaves(
            Long employeeId,
            Long leaveTypeId){
        return getLeaveBalance(employeeId, leaveTypeId).getAllocatedLeaves();
    }

    public Float availableLeaves(
            Long employeeId,
            Long leaveTypeId){
        return getLeaveBalance(employeeId, leaveTypeId).getAvailableLeaves();
    }

    public Float carryForwardLeaves(
            Long employeeId,
            Long leaveTypeId){
        return getLeaveBalance(employeeId, leaveTypeId).getCarryForwardLeaves();
    }

    public LeaveBalance reserveLeaveBalance(
            Long employeeId,
            Long leaveTypeId,
            Float leaveDays){
        LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId);
        if(leaveBalance.getAvailableLeaves() >= leaveDays) {
            leaveBalance.setAvailableLeaves(leaveBalance.getAvailableLeaves() - leaveDays);
            leaveBalance.setPendingLeaves(leaveDays);
            return leaveBalanceRepo.save(leaveBalance);
        }
        throw new BadRequestException("Requested leave days are more than available days, therefore not allowed.");
    }

    public LeaveBalance approveReservedLeave(
            Long employeeId,
            Long leaveTypeId,
            float leaveDays){
        LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId);
        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + leaveDays);
        leaveBalance.setPendingLeaves(leaveBalance.getPendingLeaves() - leaveDays);
        return leaveBalanceRepo.save(leaveBalance);
    }

    public LeaveBalance releaseReservedLeave(
            Long employeeId,
            Long leaveTypeId,
            float leaveDays){
        LeaveBalance leaveBalance = getLeaveBalance(employeeId, leaveTypeId);
        leaveBalance.setAvailableLeaves(leaveBalance.getAvailableLeaves() + leaveDays);
        leaveBalance.setPendingLeaves(leaveBalance.getPendingLeaves() - leaveDays);
        return leaveBalanceRepo.save(leaveBalance);
    }

    private String getFinancialYear(LocalDate date) {
        int year = date.getYear();
        if (date.getMonthValue() >= 4) {
            return year + "-" + String.valueOf(year + 1).substring(2);
        }
        return (year - 1) + "-" + String.valueOf(year).substring(2);
    }
}
