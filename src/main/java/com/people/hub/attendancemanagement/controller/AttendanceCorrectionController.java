package com.people.hub.attendancemanagement.controller;

import com.people.hub.attendancemanagement.dto.CorrectionDecisionRequest;
import com.people.hub.attendancemanagement.dto.CorrectionResponse;
import com.people.hub.attendancemanagement.dto.CreateCorrectionRequest;
import com.people.hub.attendancemanagement.model.AttendanceCorrection;
import com.people.hub.attendancemanagement.service.AttendanceCorrectionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance-corrections")
public class AttendanceCorrectionController {

    private final AttendanceCorrectionService correctionService;

    public AttendanceCorrectionController(AttendanceCorrectionService correctionService) {
        this.correctionService = correctionService;
    }

    /** Employee files a correction for themself, or a manager files on
     * behalf of a report — adapt the requestedBy resolution to however your
     * Authentication principal exposes employeeId. */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public CorrectionResponse fileRequest(@RequestBody CreateCorrectionRequest request,
                                          Authentication authentication) {
        Long requestedByEmployeeId = extractEmployeeId(authentication);
        AttendanceCorrection saved = correctionService.fileRequest(request, requestedByEmployeeId);
        return new CorrectionResponse(saved);
    }

    @PostMapping("/{correctionId}/approve")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public CorrectionResponse approve(@PathVariable Long correctionId,
                                      @RequestBody CorrectionDecisionRequest decision,
                                      Authentication authentication) {
        Long decidedBy = extractEmployeeId(authentication);
        AttendanceCorrection saved = correctionService.approve(correctionId, decidedBy, decision.getDecisionNote());
        return new CorrectionResponse(saved);
    }

    @PostMapping("/{correctionId}/reject")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public CorrectionResponse reject(@PathVariable Long correctionId,
                                     @RequestBody CorrectionDecisionRequest decision,
                                     Authentication authentication) {
        Long decidedBy = extractEmployeeId(authentication);
        AttendanceCorrection saved = correctionService.reject(correctionId, decidedBy, decision.getDecisionNote());
        return new CorrectionResponse(saved);
    }

    @PostMapping("/{correctionId}/withdraw")
    @PreAuthorize("isAuthenticated()")
    public CorrectionResponse withdraw(@PathVariable Long correctionId) {
        AttendanceCorrection saved = correctionService.withdraw(correctionId);
        return new CorrectionResponse(saved);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public List<CorrectionResponse> getPendingQueue() {
        return correctionService.getPendingQueue().stream().map(CorrectionResponse::new).toList();
    }

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('HR_ADMIN') or #employeeId == authentication.principal.employeeId")
    public List<CorrectionResponse> getHistory(@PathVariable Long employeeId) {
        return correctionService.getHistoryForEmployee(employeeId).stream().map(CorrectionResponse::new).toList();
    }

    /** Placeholder — replace with however your security principal actually
     * exposes the authenticated employee's internal ID (custom UserDetails,
     * JWT claim extraction, etc). Left unimplemented deliberately rather
     * than guessing your auth setup. */
    private Long extractEmployeeId(Authentication authentication) {
        throw new UnsupportedOperationException(
                "Wire this up to your actual UserPrincipal/JWT claims to extract employeeId");
    }
}