package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.dto.LeaveBalanceDto;
import com.people.hub.leavemanagement.service.LeaveAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allocation")
@RequiredArgsConstructor
public class LeaveAllocationController {
    private final LeaveAllocationService allocationService;

    @PostMapping
    public ResponseEntity<RestApiResponse> allocateLeave(@RequestBody LeaveBalanceDto leaveBalanceDto){
        return ResponseEntity.status(201).body(allocationService.allocateLeave(leaveBalanceDto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<RestApiResponse> allocateLeaveBulk(@RequestBody List<LeaveBalanceDto> leaveBalanceDtoList){
        return ResponseEntity.ok(allocationService.allocateLeaveBulk(leaveBalanceDtoList));
    }

    @PostMapping("/default-allocation")
    public ResponseEntity<RestApiResponse> allocateAnnualLeaves(){
        return ResponseEntity.ok(allocationService.allocateAnnualLeaves());
    }

    @PostMapping("/{employeeId}")
    public ResponseEntity<RestApiResponse> allocateAnnualLeaves(@PathVariable Long employeeId){
        return ResponseEntity.ok(allocationService.allocateAnnualLeaves(employeeId));
    }

    @PostMapping("/adjust/allocation/{leaveBalanceId}")
    public ResponseEntity<RestApiResponse> adjustLeaveBalance(
            @PathVariable Long leaveBalanceId,
            @RequestParam Float days,
            @RequestParam String remarks){
        return ResponseEntity.ok(allocationService.adjustLeaveBalance(leaveBalanceId, days, remarks));
    }

    @PostMapping("/adjust/carry-forward/{leaveBalanceId}")
    public ResponseEntity<RestApiResponse> adjustCarryForward(
            @PathVariable Long leaveBalanceId,
            @RequestParam Float days,
            @RequestParam String remarks){
        return ResponseEntity.ok(allocationService.adjustCarryForward(leaveBalanceId, days, remarks));
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<RestApiResponse> getAllocationHistory(@PathVariable Long employeeId){
        return ResponseEntity.ok(allocationService.getAllocationHistory(employeeId));
    }

    @GetMapping("/{employeeId}/{leaveTypeId}")
    public ResponseEntity<RestApiResponse> getAllocationHistory(
            Long employeeId,
            Long leaveTypeId){
        return ResponseEntity.ok(allocationService.getAllocationHistory(employeeId, leaveTypeId));
    }
}
