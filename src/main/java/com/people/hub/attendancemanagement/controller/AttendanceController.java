package com.people.hub.attendancemanagement.controller;

import com.company.attendance.dto.PunchResponse;
import com.company.attendance.service.AttendanceQueryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Normal JSON REST API consumed by the frontend dashboard / mobile app /
 * reporting module. Unlike AdmsDeviceController, this DOES sit behind your
 * standard auth (JWT/session) — secured here via method-level @PreAuthorize
 * as an example; adapt to whatever your SecurityConfig uses.
 */
@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    private final AttendanceQueryService queryService;

    public AttendanceController(AttendanceQueryService queryService) {
        this.queryService = queryService;
    }

    /** Employees can view their own punches; HR/admin can view via the employeeId param freely
     *  (enforce that authorization distinction in a real SecurityConfig / method check). */
    @GetMapping("/employees/{employeeId}/punches")
    @PreAuthorize("hasRole('HR_ADMIN') or #employeeId == authentication.principal.employeeId")
    public List<PunchResponse> getPunchesForDay(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return queryService.getPunchesForEmployeeOnDate(employeeId, date);
    }

    /** Ops/reconciliation view: punches that arrived but couldn't be matched
     * to an employee (unmapped device PIN) — needs fixing in EmployeeDeviceMapping. */
    @GetMapping("/punches/unresolved")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public List<PunchResponse> getUnresolvedPunches() {
        return queryService.getUnresolvedPunches();
    }
}
