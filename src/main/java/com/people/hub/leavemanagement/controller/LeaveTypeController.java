package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.dto.LeaveTypeDto;
import com.people.hub.leavemanagement.enums.LeaveTypeName;
import com.people.hub.leavemanagement.model.LeaveType;
import com.people.hub.leavemanagement.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-type")
@RequiredArgsConstructor
public class LeaveTypeController {
    private final LeaveTypeService typeService;

    @PostMapping
    public ResponseEntity<LeaveType> createLeaveType(@RequestBody LeaveTypeDto leaveTypeDto){
        return ResponseEntity.status(201).body(typeService.createLeaveType(leaveTypeDto));
    }

    @PostMapping("/{leaveTypeId}")
    public ResponseEntity<LeaveType> updateLeaveType(
            @PathVariable Long leaveTypeId,
            @RequestBody LeaveTypeDto leaveTypeDto) {
        return ResponseEntity.ok(typeService.updateLeaveType(leaveTypeId, leaveTypeDto));
    }

    @GetMapping("/{leaveTypeId}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.getLeaveTypeById(leaveTypeId));
    }

    @GetMapping("/by-name/{leaveTypeName}")
    public ResponseEntity<LeaveType> getLeaveType(@PathVariable LeaveTypeName leaveTypeName) {
        return ResponseEntity.ok(typeService.getLeaveType(leaveTypeName));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getAllLeaveTypes() {
        return ResponseEntity.ok(typeService.getAllLeaveTypes());
    }

    @GetMapping("/active")
    public ResponseEntity<RestApiResponse> getActiveLeaveTypes() {
        return ResponseEntity.ok(typeService.getActiveLeaveTypes());
    }

    @PostMapping("/activate/{leaveTypeId}")
    public ResponseEntity<LeaveType> activateLeaveType(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.activateLeaveType(leaveTypeId));
    }

    @PostMapping("/deactivate/{leaveTypeId}")
    public ResponseEntity<LeaveType> deactivateLeaveType(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.deactivateLeaveType(leaveTypeId));
    }

    @PostMapping("/delete/{leaveTypeId}")
    public ResponseEntity<RestApiResponse> deleteLeaveType(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.deleteLeaveType(leaveTypeId));
    }

    @GetMapping("/exists/{leaveTypeName}")
    public ResponseEntity<Boolean> exists(@PathVariable LeaveTypeName leaveTypeName) {
        return ResponseEntity.ok(typeService.exists(leaveTypeName));
    }

    @GetMapping("/active/{leaveTypeId}")
    public ResponseEntity<Boolean> isActive(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.isActive(leaveTypeId));
    }

    @GetMapping("/annual-allocation/{leaveTypeId}")
    public ResponseEntity<Float> getAnnualAllocation(@PathVariable Long leaveTypeId) {
        return ResponseEntity.ok(typeService.getAnnualAllocation(leaveTypeId));
    }

    @GetMapping("/carry-forward/{leaveTypeId}")
    public ResponseEntity<Boolean> isCarryForwardAllowed(@PathVariable Long leaveTypeId){
        return ResponseEntity.ok(typeService.isCarryForwardAllowed(leaveTypeId));
    }

    @GetMapping("/max-carry-forward/{leaveTypeId}")
    public ResponseEntity<Float> getMaxCarryForward(@PathVariable Long leaveTypeId){
        return ResponseEntity.ok(typeService.getMaxCarryForward(leaveTypeId));
    }

    @GetMapping("/carry-forward/enabled")
    public ResponseEntity<RestApiResponse> getCarryForwardEnabledLeaveTypes() {
        return ResponseEntity.ok(typeService.getCarryForwardEnabledLeaveTypes());
    }

    @GetMapping("/allocable")
    public ResponseEntity<RestApiResponse> getAllocatableLeaveTypes() {
        return ResponseEntity.ok(typeService.getAllocatableLeaveTypes());
    }
}
