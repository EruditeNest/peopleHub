package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.dto.LeaveBalanceDto;
import com.people.hub.leavemanagement.model.LeaveBalance;
import com.people.hub.leavemanagement.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-balance")
@RequiredArgsConstructor
public class LeaveBalanceController {
    private final LeaveBalanceService balanceService;

    @PostMapping
    public ResponseEntity<LeaveBalance> createLeaveBalance(@RequestBody LeaveBalanceDto leaveBalanceDto){
        return ResponseEntity.status(201).body(balanceService.createLeaveBalance(leaveBalanceDto));
    }

    @PostMapping("/{leaveBalanceId}")
    public ResponseEntity<LeaveBalance> updateLeaveBalance(@PathVariable Long leaveBalanceId, @RequestParam float days){
        return ResponseEntity.ok(balanceService.updateLeaveBalance(leaveBalanceId, days));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceById(@PathVariable Long id){
        return ResponseEntity.ok(balanceService.getLeaveBalanceById(id));
    }

    @GetMapping("/{employeeId}/{leaveTypeId}")
    public ResponseEntity<LeaveBalance> getLeaveBalance(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId){
        return ResponseEntity.ok(balanceService.getLeaveBalance(employeeId, leaveTypeId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<RestApiResponse> getEmployeeLeaveBalances(@PathVariable Long employeeId){
        return ResponseEntity.ok(balanceService.getEmployeeLeaveBalances(employeeId));
    }

    @GetMapping("/allocated/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Float> allocatedLeaves(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId){
        return ResponseEntity.ok(balanceService.allocatedLeaves(employeeId, leaveTypeId));
    }

    @GetMapping("/available/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Float> availableLeaves(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId){
        return ResponseEntity.ok(balanceService.availableLeaves(employeeId, leaveTypeId));
    }

    @GetMapping("/carry-forward/{employeeId}/{leaveTypeId}")
    public ResponseEntity<Float> carryForwardLeaves(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId){
        return ResponseEntity.ok(balanceService.carryForwardLeaves(employeeId, leaveTypeId));
    }
}
