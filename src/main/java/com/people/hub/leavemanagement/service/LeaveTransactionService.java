package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.enums.LeaveTransactionSource;
import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import com.people.hub.leavemanagement.model.LeaveTransaction;
import com.people.hub.leavemanagement.repository.LeaveTransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTransactionService {
    private final LeaveTransactionRepo transactionRepo;

    public LeaveTransaction createTransaction(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            LeaveTransactionSource transactionSource,
            LeaveTransactionType transactionType,
            float days,
            String remarks) {
        LeaveTransaction leaveTransaction = new LeaveTransaction();
        leaveTransaction.setEmployeeId(employeeId);
        leaveTransaction.setLeaveTypeId(leaveTypeId);
        leaveTransaction.setLeaveApplicationId(leaveApplicationId);
        leaveTransaction.setLeaveTransactionSource(transactionSource);
        leaveTransaction.setLeaveTransactionType(transactionType);
        leaveTransaction.setDays(days);
        leaveTransaction.setRemarks(remarks);
        leaveTransaction.setTransactionDate(LocalDateTime.now());
        return transactionRepo.save(leaveTransaction);
    }

    public Float getTotalDaysByTransactionType(Long employeeId, Long leaveTypeId, LeaveTransactionType transactionType) {
        return transactionRepo.getTotalDaysByTransactionType(employeeId, leaveTypeId, transactionType);
    }

    public RestApiResponse getEmployeeTransactions(Long employeeId){
        List<LeaveTransaction> leaveTransactionList = transactionRepo.findByEmployeeId(employeeId);
        return RestApiResponse.success(leaveTransactionList);
    }

    public RestApiResponse getLeaveTransactions(Long leaveApplicationId) {
        List<LeaveTransaction> leaveTransactionList = transactionRepo.findByLeaveApplicationId(leaveApplicationId);
        return RestApiResponse.success(leaveTransactionList);
    }

    public RestApiResponse getTransactionsByType(LeaveTransactionType transactionType) {
        List<LeaveTransaction> leaveTransactionList = transactionRepo.findByLeaveTransactionType(transactionType);
        return RestApiResponse.success(leaveTransactionList);
    }

    public Integer getTotalAppliedTransactions(Long employeeId, Long leaveTypeId) {
        return transactionRepo.findByEmployeeIdAndLeaveTypeIdAndLeaveTransactionType(employeeId, leaveTypeId, LeaveTransactionType.APPLY).size();
    }

    public Integer getTotalApprovedTransactions(Long employeeId, Long leaveTypeId) {
        return transactionRepo.findByEmployeeIdAndLeaveTypeIdAndLeaveTransactionType(employeeId, leaveTypeId, LeaveTransactionType.APPROVE).size();
    }

    public Integer getTotalCancelledTransactions(Long employeeId, Long leaveTypeId) {
        return transactionRepo.findByEmployeeIdAndLeaveTypeIdAndLeaveTransactionType(employeeId, leaveTypeId, LeaveTransactionType.CANCEL).size();
    }

    public Integer getTotalAdjustments(Long employeeId, Long leaveTypeId) {
        return transactionRepo.findByEmployeeIdAndLeaveTypeIdAndLeaveTransactionType(employeeId, leaveTypeId, LeaveTransactionType.ADJUSTMENT).size();
    }

    public LeaveTransaction recordCancellation(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                leaveApplicationId,
                LeaveTransactionSource.APPLICATION,
                LeaveTransactionType.CANCEL,
                days,
                remarks);
    }

    public LeaveTransaction recordHold(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                leaveApplicationId,
                LeaveTransactionSource.APPLICATION,
                LeaveTransactionType.HOLD,
                days,
                remarks);
    }

    public LeaveTransaction recordRejection(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                leaveApplicationId,
                LeaveTransactionSource.APPLICATION,
                LeaveTransactionType.REJECT,
                days,
                remarks);
    }

    public LeaveTransaction recordApproval(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                leaveApplicationId,
                LeaveTransactionSource.APPLICATION,
                LeaveTransactionType.APPROVE,
                days,
                remarks);
    }

    public LeaveTransaction recordApplication(
            Long employeeId,
            Long leaveTypeId,
            Long leaveApplicationId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                leaveApplicationId,
                LeaveTransactionSource.APPLICATION,
                LeaveTransactionType.APPLY,
                days,
                remarks);
    }

    public LeaveTransaction recordAllocation(
            Long employeeId,
            Long leaveTypeId,
            float days,
            String remarks) {

        return createTransaction(
                employeeId,
                leaveTypeId,
                null,
                LeaveTransactionSource.ALLOCATION,
                LeaveTransactionType.ALLOCATION,
                days,
                remarks);
    }
}
