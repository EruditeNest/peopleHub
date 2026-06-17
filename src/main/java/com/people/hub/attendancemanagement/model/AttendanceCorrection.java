package com.people.hub.attendancemanagement.model;

import com.people.hub.attendancemanagement.enums.CorrectionStatus;
import com.people.hub.attendancemanagement.enums.CorrectionType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A request to override the computed work-hours for one employee on one day.
 *
 * Design principle: this NEVER touches AttendancePunch rows. The raw device
 * log stays exactly as received, forever, for audit purposes. A correction
 * only affects the derived DailyWorkRecord, and only once APPROVED.
 *
 * One correction request targets exactly one (employeeId, workDate) pair.
 * If a second request is raised for a day that already has a PENDING
 * request, reject it at the service layer rather than allowing two open
 * requests to race — see AttendanceCorrectionService.
 */
@Entity
@Table(
        name = "attendance_correction",
        indexes = {
                @Index(name = "idx_correction_employee_date", columnList = "employee_id, work_date"),
                @Index(name = "idx_correction_status", columnList = "status")
        }
)
public class AttendanceCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "correction_type", nullable = false, length = 30)
    private CorrectionType correctionType;

    /** Requested corrected punch-in time. Nullable depending on correctionType
     * (e.g. not used for MANUAL_DAY_OVERRIDE). */
    @Column(name = "requested_in_time")
    private LocalDateTime requestedInTime;

    @Column(name = "requested_out_time")
    private LocalDateTime requestedOutTime;

    /** Used for MANUAL_DAY_OVERRIDE — a flat worked-minutes value instead of
     * in/out times, for cases like approved field duty with no device access. */
    @Column(name = "requested_worked_minutes")
    private Integer requestedWorkedMinutes;

    @Column(name = "reason", nullable = false, length = 1000)
    private String reason;

    /** Supporting evidence reference (e.g. uploaded approval email, manager
     * chat screenshot) — store a document/attachment ID, not the file itself. */
    @Column(name = "supporting_document_ref", length = 200)
    private String supportingDocumentRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CorrectionStatus status;

    @Column(name = "requested_by_employee_id", nullable = false)
    private Long requestedByEmployeeId; // usually == employeeId, but a manager can file on someone's behalf

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "decided_by_employee_id")
    private Long decidedByEmployeeId;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "decision_note", length = 1000)
    private String decisionNote;

    @Version
    @Column(name = "version")
    private Long version;

    protected AttendanceCorrection() {
        // JPA
    }

    public AttendanceCorrection(Long employeeId, LocalDate workDate, CorrectionType correctionType,
                                LocalDateTime requestedInTime, LocalDateTime requestedOutTime,
                                Integer requestedWorkedMinutes, String reason,
                                String supportingDocumentRef, Long requestedByEmployeeId,
                                LocalDateTime requestedAt) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.correctionType = correctionType;
        this.requestedInTime = requestedInTime;
        this.requestedOutTime = requestedOutTime;
        this.requestedWorkedMinutes = requestedWorkedMinutes;
        this.reason = reason;
        this.supportingDocumentRef = supportingDocumentRef;
        this.status = CorrectionStatus.PENDING;
        this.requestedByEmployeeId = requestedByEmployeeId;
        this.requestedAt = requestedAt;
    }

    public void approve(Long decidedByEmployeeId, LocalDateTime decidedAt, String decisionNote) {
        requireStatus(CorrectionStatus.PENDING, "approve");
        this.status = CorrectionStatus.APPROVED;
        this.decidedByEmployeeId = decidedByEmployeeId;
        this.decidedAt = decidedAt;
        this.decisionNote = decisionNote;
    }

    public void reject(Long decidedByEmployeeId, LocalDateTime decidedAt, String decisionNote) {
        requireStatus(CorrectionStatus.PENDING, "reject");
        this.status = CorrectionStatus.REJECTED;
        this.decidedByEmployeeId = decidedByEmployeeId;
        this.decidedAt = decidedAt;
        this.decisionNote = decisionNote;
    }

    public void withdraw(LocalDateTime withdrawnAt) {
        requireStatus(CorrectionStatus.PENDING, "withdraw");
        this.status = CorrectionStatus.WITHDRAWN;
        this.decidedAt = withdrawnAt;
    }

    private void requireStatus(CorrectionStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Cannot " + action + " correction " + id + " in status " + this.status);
        }
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDate getWorkDate() { return workDate; }
    public CorrectionType getCorrectionType() { return correctionType; }
    public LocalDateTime getRequestedInTime() { return requestedInTime; }
    public LocalDateTime getRequestedOutTime() { return requestedOutTime; }
    public Integer getRequestedWorkedMinutes() { return requestedWorkedMinutes; }
    public String getReason() { return reason; }
    public String getSupportingDocumentRef() { return supportingDocumentRef; }
    public CorrectionStatus getStatus() { return status; }
    public Long getRequestedByEmployeeId() { return requestedByEmployeeId; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public Long getDecidedByEmployeeId() { return decidedByEmployeeId; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public String getDecisionNote() { return decisionNote; }
    public Long getVersion() { return version; }
}