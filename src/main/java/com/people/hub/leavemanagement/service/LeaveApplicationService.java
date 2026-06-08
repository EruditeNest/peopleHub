package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.leavemanagement.dto.LeaveApplicationDto;
import com.people.hub.leavemanagement.enums.ActionRole;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveApplication;
import com.people.hub.leavemanagement.repository.LeaveApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {
    private final LeaveApplicationRepo applicationRepo;
    private final LeaveBalanceService balanceService;
    private final LeaveActionHistoryService actionHistoryService;
    private final LeaveTransactionService transactionService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "employeeId",
            "holidayName",
            "optionalHoliday",
            "created_at",
            "updated_at"
    );


    @Transactional
    public LeaveApplication applyLeave(LeaveApplicationDto leaveApplicationDto, Long employeeId){
        LeaveApplication leaveApplication = new LeaveApplication();
        leaveApplication.setLeaveTypeId(leaveApplicationDto.getLeaveTypeId());
        leaveApplication.setEmployeeId(employeeId);
        leaveApplication.setAppliedAt(LocalDateTime.now());
        leaveApplication.setStartDate(leaveApplicationDto.getStartDate());
        leaveApplication.setEndDate(leaveApplicationDto.getEndDate());
        leaveApplication.setReason(leaveApplication.getReason());
        leaveApplication.setStatus(LeaveStatus.PENDING);
        leaveApplication.setHrId(leaveApplicationDto.getHrId());
        leaveApplication.setManagerId(leaveApplicationDto.getManagerId());

        float totalDays = calculateLeaveDays(leaveApplicationDto.getStartDate(), leaveApplicationDto.getEndDate());
        leaveApplication.setTotalDays(totalDays);
        leaveApplication = applicationRepo.save(leaveApplication);

        if(balanceService.availableLeaves(employeeId, leaveApplicationDto.getLeaveTypeId()) >= totalDays) {
            balanceService.reserveLeaveBalance(employeeId, leaveApplicationDto.getLeaveTypeId(), totalDays);
        }
        actionHistoryService.createHistory(
                leaveApplication.getId(),
                employeeId,
                ActionRole.EMPLOYEE,
                LeaveStatus.PENDING,
                LeaveStatus.PENDING,
                "Employee applied leave for " + totalDays);
        transactionService.recordApplication(
                employeeId,
                leaveApplicationDto.getLeaveTypeId(),
                leaveApplication.getId(),
                totalDays,
                "Employee applied leave for " + totalDays);

        return leaveApplication;
    }

    public LeaveApplication getLeaveById(Long leaveId){
        return applicationRepo.findById(leaveId)
                .orElseThrow(() -> new NotFoundException("Leave Application not found", leaveId));
    }

    public RestApiResponse getMyAllLeaves(Long employeeId){
        List<LeaveApplication> leaveApplications = applicationRepo.findByEmployeeId(employeeId);
        return RestApiResponse.success(leaveApplications);
    }

    public RestApiResponse getMyLeaves(Long employeeId, int page, int size, String sortField, String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS);
        Page<LeaveApplication> leaveApplications = applicationRepo.findAllByEmployeeId(
                employeeId,
                pageable);
        PageInfo pageInfo = new PageInfo(
                leaveApplications.getNumber(),
                leaveApplications.getSize(),
                leaveApplications.getTotalElements());
        return RestApiResponse.success(pageInfo, leaveApplications.getContent());
    }

    @Transactional
    public RestApiResponse cancelLeave(Long leaveId, String remarks, Long employeeId){
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setStatus(LeaveStatus.CANCELLED);

        balanceService.releaseReservedLeave(leaveApplication.getEmployeeId(), leaveApplication.getLeaveTypeId(), leaveApplication.getTotalDays());
        actionHistoryService.createHistory(
                leaveApplication.getId(),
                employeeId,
                ActionRole.EMPLOYEE,
                leaveApplication.getStatus(),
                LeaveStatus.CANCELLED,
                remarks);
        transactionService.recordCancellation(
                employeeId,
                leaveApplication.getLeaveTypeId(),
                leaveApplication.getId(),
                leaveApplication.getTotalDays(),
                remarks);

        applicationRepo.save(leaveApplication);
        return RestApiResponse.success("leave cancelled successfully");
    }

    // Manager Actions
    public RestApiResponse getAllPendingLeavesForManager(Long managerId) {
        List<LeaveApplication> applicationList = applicationRepo.findByManagerIdAndStatus(managerId, LeaveStatus.PENDING);
        return RestApiResponse.success(applicationList);
    }

    public RestApiResponse getPendingLeavesForManager(
            Long managerId,
            int page,
            int size,
            String sortField,
            String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS);
        Page<LeaveApplication> leaveApplications = applicationRepo.findByManagerIdAndStatus(
                managerId,
                LeaveStatus.PENDING,
                pageable);
        PageInfo pageInfo = new PageInfo(
                leaveApplications.getNumber(),
                leaveApplications.getSize(),
                leaveApplications.getTotalElements());
        return RestApiResponse.success(pageInfo, leaveApplications.getContent());
    }

    public LeaveApplication approveByManager(Long leaveId, String remarks){
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setManagerRemarks(remarks);
        leaveApplication.setApprovedAt(LocalDateTime.now());
        leaveApplication.setStatus(LeaveStatus.MANAGER_APPROVED);

        balanceService.approveReservedLeave(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveApplication.getTotalDays());
        actionHistoryService.createHistory(
                leaveId,
                leaveApplication.getManagerId(),
                ActionRole.MANAGER,
                leaveApplication.getStatus(),
                LeaveStatus.MANAGER_APPROVED,
                remarks);
        transactionService.recordApproval(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveId,
                leaveApplication.getTotalDays(),
                remarks);
        return applicationRepo.save(leaveApplication);
    }

    public LeaveApplication rejectByManager(Long leaveId, String remarks) {
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setManagerRemarks(remarks);
        leaveApplication.setRejectedAt(LocalDateTime.now());
        leaveApplication.setStatus(LeaveStatus.MANAGER_REJECTED);

        balanceService.releaseReservedLeave(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveApplication.getTotalDays());
        actionHistoryService.createHistory(
                leaveId,
                leaveApplication.getManagerId(),
                ActionRole.MANAGER,
                leaveApplication.getStatus(),
                LeaveStatus.MANAGER_REJECTED,
                remarks);
        transactionService.recordRejection(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveId,
                leaveApplication.getTotalDays(),
                remarks);
        return applicationRepo.save(leaveApplication);
    }

    // HR Actions
    public RestApiResponse getPendingLeavesForHr(Long hrId) {
        List<LeaveApplication> applicationList = applicationRepo.findByHrIdAndStatus(hrId, LeaveStatus.PENDING);
        return RestApiResponse.success(applicationList);
    }

    public RestApiResponse getPendingLeavesForHr(
            Long hrId,
            int page,
            int size,
            String sortField,
            String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS);
        Page<LeaveApplication> leaveApplications = applicationRepo.findByHrIdAndStatus(
                hrId,
                LeaveStatus.PENDING,
                pageable);
        PageInfo pageInfo = new PageInfo(
                leaveApplications.getNumber(),
                leaveApplications.getSize(),
                leaveApplications.getTotalElements());
        return RestApiResponse.success(pageInfo, leaveApplications.getContent());
    }

    public LeaveApplication approveByHr(Long leaveId, String remarks){
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setHrRemarks(remarks);
        leaveApplication.setApprovedAt(LocalDateTime.now());
        leaveApplication.setStatus(LeaveStatus.HR_APPROVED);

        balanceService.approveReservedLeave(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveApplication.getTotalDays());
        actionHistoryService.createHistory(
                leaveId,
                leaveApplication.getHrId(),
                ActionRole.HR,
                leaveApplication.getStatus(),
                LeaveStatus.HR_APPROVED,
                remarks);
        transactionService.recordApproval(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveId,
                leaveApplication.getTotalDays(),
                remarks);
        return applicationRepo.save(leaveApplication);
    }

    public LeaveApplication rejectByHr(Long leaveId, String remarks, Long hrId) {
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setHrRemarks(remarks);
        leaveApplication.setRejectedAt(LocalDateTime.now());
        leaveApplication.setStatus(LeaveStatus.HR_REJECTED);

        balanceService.releaseReservedLeave(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveApplication.getTotalDays());
        actionHistoryService.createHistory(
                leaveId,
                leaveApplication.getHrId(),
                ActionRole.HR,
                leaveApplication.getStatus(),
                LeaveStatus.HR_REJECTED,
                remarks);
        transactionService.recordRejection(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveId,
                leaveApplication.getTotalDays(),
                remarks);
        return applicationRepo.save(leaveApplication);
    }

    public LeaveApplication holdByHr(Long leaveId, String remarks, Long hrId) {
        LeaveApplication leaveApplication = getLeaveById(leaveId);
        leaveApplication.setHrRemarks(remarks);
        leaveApplication.setStatus(LeaveStatus.ON_HOLD);

        actionHistoryService.createHistory(
                leaveId,
                leaveApplication.getHrId(),
                ActionRole.HR,
                leaveApplication.getStatus(),
                LeaveStatus.ON_HOLD,
                remarks);
        transactionService.recordHold(
                leaveApplication.getEmployeeId(),
                leaveApplication.getLeaveTypeId(),
                leaveId,
                leaveApplication.getTotalDays(),
                remarks);
        return applicationRepo.save(leaveApplication);
    }

    // Dashboard / Reporting
    public RestApiResponse getLeavesByEmployeeAndStatus(Long employeeId, LeaveStatus status) {
        List<LeaveApplication> leaveApplicationList = applicationRepo.findByEmployeeIdAndStatus(employeeId, status);
        return RestApiResponse.success(leaveApplicationList);
    }

    public RestApiResponse getLeavesByDateRange(LocalDateTime fromDate, LocalDateTime toDate){
        List<LeaveApplication> leaveApplicationList = applicationRepo.findByAppliedAtBetween(fromDate, toDate);
        return RestApiResponse.success(leaveApplicationList);
    }

    public RestApiResponse getPendingManagerApprovals(Long managerId) {
        List<LeaveApplication> leaveApplicationList = applicationRepo.findByManagerIdAndStatus(managerId, LeaveStatus.PENDING);
        return RestApiResponse.success(leaveApplicationList);
    }

    private float calculateLeaveDays(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        long totalHours = Duration.between(startDate, endDate).toHours();
        float days = Math.round(totalHours / 24.0f);
        return Math.round(days * 2) / 2.0f;
    }
}
