package com.people.hub.attendancemanagement.controller;

import com.people.hub.attendancemanagement.dto.DailyWorkRecordResponse;
import com.people.hub.attendancemanagement.model.DailyWorkRecord;
import com.people.hub.attendancemanagement.repository.DailyWorkRecordRepository;
import com.people.hub.attendancemanagement.service.WorkHoursCalculationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/work-hours")
public class WorkHoursController {

    private final DailyWorkRecordRepository workRecordRepository;
    private final WorkHoursCalculationService calculationService;

    public WorkHoursController(DailyWorkRecordRepository workRecordRepository,
                               WorkHoursCalculationService calculationService) {
        this.workRecordRepository = workRecordRepository;
        this.calculationService = calculationService;
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('HR_ADMIN') or #employeeId == authentication.principal.employeeId")
    public List<DailyWorkRecordResponse> getRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return workRecordRepository
                .findByEmployeeIdAndWorkDateBetweenOrderByWorkDateAsc(employeeId, from, to)
                .stream()
                .map(DailyWorkRecordResponse::new)
                .toList();
    }

    @GetMapping("/employees/{employeeId}/{date}")
    @PreAuthorize("hasRole('HR_ADMIN') or #employeeId == authentication.principal.employeeId")
    public ResponseEntity<DailyWorkRecordResponse> getSingleDay(
            @PathVariable Long employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return workRecordRepository.findByEmployeeIdAndWorkDate(employeeId, date)
                .map(DailyWorkRecordResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Manual trigger for HR to force a recompute — e.g. after fixing a
     * device-PIN mapping for a previously-unresolved punch, or investigating
     * a discrepancy. Normal flow recalculates automatically via punch events
     * and correction approvals; this is an explicit escape hatch. */
    @PostMapping("/employees/{employeeId}/{date}/recalculate")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public DailyWorkRecordResponse forceRecalculate(
            @PathVariable Long employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyWorkRecord record = calculationService.recalculate(employeeId, date);
        return new DailyWorkRecordResponse(record);
    }
}