package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.dto.LeaveApplicationDto;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveApplication;
import com.people.hub.leavemanagement.service.LeaveApplicationService;
import com.people.hub.security.MyUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveApplicationController {
    private final LeaveApplicationService applicationService;

    @PostMapping("/apply")
    public ResponseEntity<LeaveApplication> applyLeave(
            @RequestBody LeaveApplicationDto leaveApplicationDto,
            @AuthenticationPrincipal MyUserDetail userDetail){
        return ResponseEntity.status(201).body(applicationService.applyLeave(leaveApplicationDto, userDetail.getUserId()));
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveApplication> getLeaveById(@PathVariable Long leaveId){
        return ResponseEntity.ok(applicationService.getLeaveById(leaveId));
    }

    @GetMapping("/all")
    public ResponseEntity<RestApiResponse> getAllEmployeeLeaves(
            @AuthenticationPrincipal MyUserDetail userDetail){
        return ResponseEntity.ok(applicationService.getMyAllLeaves(userDetail.getUserId()));
    }

    @GetMapping("/all/with-page/{employeeId}")
    public ResponseEntity<RestApiResponse> getAllEmployeeLeaves(
            @PathVariable Long employeeId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "created_at") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(applicationService.getMyLeaves(employeeId, page, size, sortField, sortOrder));
    }

    @PostMapping("/cancel/{leaveId}")
    public ResponseEntity<RestApiResponse> cancelLeave(
            @PathVariable Long leaveId,
            @RequestParam String remarks,
            @AuthenticationPrincipal MyUserDetail userDetail){

        return ResponseEntity.ok(applicationService.cancelLeave(leaveId, remarks, userDetail.getUserId()));
    }

    @GetMapping("/manager")
    public ResponseEntity<RestApiResponse> getAllPendingLeavesForManager(
            @AuthenticationPrincipal MyUserDetail userDetail){

        return ResponseEntity.ok(applicationService.getAllPendingLeavesForManager(userDetail.getUserId()));
    }

    @GetMapping("/manager/with-page")
    public ResponseEntity<RestApiResponse> getPendingLeavesForManager(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "created_at") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @AuthenticationPrincipal MyUserDetail userDetail){
        return ResponseEntity.ok(applicationService.getPendingLeavesForManager(userDetail.getUserId(), page, size, sortField, sortOrder));
    }

    @PostMapping("/manager/approve/{leaveId}")
    public ResponseEntity<LeaveApplication> approveByManager(
            @PathVariable Long leaveId,
            @RequestParam String remarks){
        return ResponseEntity.ok(applicationService.approveByManager(leaveId, remarks));
    }

    @PostMapping("/manager/reject/{leaveId}")
    public ResponseEntity<LeaveApplication> rejectByManager(
            @PathVariable Long leaveId,
            @RequestParam String remarks) {
        return ResponseEntity.ok(applicationService.rejectByManager(leaveId, remarks));
    }

    @GetMapping("/hr/{hrId}")
    public ResponseEntity<RestApiResponse> getPendingLeavesForHr(@PathVariable Long hrId) {
        return ResponseEntity.ok(applicationService.getPendingLeavesForHr(hrId));
    }

    @GetMapping("/hr/with-page/{hrId}")
    public ResponseEntity<RestApiResponse> getPendingLeavesForHr(
            @PathVariable Long hrId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortField,
            @RequestParam String sortOrder) {
        return ResponseEntity.ok(applicationService.getPendingLeavesForHr(hrId, page, size, sortField, sortOrder));
    }

    @PostMapping("/hr/approve/{leaveId}")
    public ResponseEntity<LeaveApplication> approveByHr(@PathVariable Long leaveId, @RequestParam String remarks){
        return ResponseEntity.ok(applicationService.approveByHr(leaveId, remarks));
    }

    @PostMapping("/hr/reject/{leaveId}")
    public ResponseEntity<LeaveApplication> rejectByHr(
            @PathVariable Long leaveId,
            @RequestParam String remarks,
            @AuthenticationPrincipal MyUserDetail userDetail) {
        return ResponseEntity.ok(applicationService.rejectByHr(leaveId, remarks, userDetail.getUserId()));
    }

    @PostMapping("/hr/hold/{leaveId}")
    public ResponseEntity<LeaveApplication> holdByHr(
            @PathVariable Long leaveId,
            @RequestParam String remarks,
            @AuthenticationPrincipal MyUserDetail userDetail) {
        return ResponseEntity.ok(applicationService.holdByHr(leaveId, remarks, userDetail.getUserId()));
    }

    @GetMapping("/{employeeId}/{status}")
    public ResponseEntity<RestApiResponse> getLeavesByEmployeeAndStatus(
            @PathVariable Long employeeId,
            @PathVariable LeaveStatus status) {
        return ResponseEntity.ok(applicationService.getLeavesByEmployeeAndStatus(employeeId, status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<RestApiResponse> getLeavesByDateRange(
            @RequestParam LocalDateTime fromDate,
            @RequestParam LocalDateTime toDate){
        return ResponseEntity.ok(applicationService.getLeavesByDateRange(fromDate, toDate));
    }

    @GetMapping("/manager/pending-approval/{managerId}")
    public ResponseEntity<RestApiResponse> getPendingManagerApprovals(@PathVariable Long managerId) {
        return ResponseEntity.ok(applicationService.getPendingManagerApprovals(managerId));
    }
}
