package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import com.people.hub.leavemanagement.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class LeaveTransactionController {
    private final LeaveTransactionService transactionService;

    @GetMapping("/total-days")
    public ResponseEntity<Float> getTotalDaysByTransactionType(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam LeaveTransactionType transactionType) {
        return ResponseEntity.ok(transactionService.getTotalDaysByTransactionType(employeeId, leaveTypeId, transactionType));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<RestApiResponse> getEmployeeTransactions(@PathVariable Long employeeId){
        return ResponseEntity.ok(transactionService.getEmployeeTransactions(employeeId));
    }

    @GetMapping("/application/{leaveApplicationId}")
    public ResponseEntity<RestApiResponse> getLeaveTransactions(@PathVariable Long leaveApplicationId) {
        return ResponseEntity.ok(transactionService.getLeaveTransactions(leaveApplicationId));
    }

    @GetMapping("/type/{transactionType}")
    public ResponseEntity<RestApiResponse> getTransactionsByType(@PathVariable LeaveTransactionType transactionType) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(transactionType));
    }

    @GetMapping("/applied/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Integer> getTotalAppliedTransactions(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(transactionService.getTotalAppliedTransactions(employeeId, leaveTypeId));
    }

    @GetMapping("/approved/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Integer> getTotalApprovedTransactions(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(transactionService.getTotalApprovedTransactions(employeeId, leaveTypeId));
    }

    @GetMapping("/cancelled/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Integer> getTotalCancelledTransactions(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(transactionService.getTotalCancelledTransactions(employeeId, leaveTypeId));
    }

    @GetMapping("/adjustments/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Integer> getTotalAdjustments(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(transactionService.getTotalAdjustments(employeeId, leaveTypeId));
    }
}
