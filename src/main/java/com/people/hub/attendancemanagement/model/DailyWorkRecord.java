package com.people.hub.attendancemanagement.model;

import com.people.hub.attendancemanagement.enums.DailyWorkRecordStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Derived/computed work-hours summary for one employee for one "attendance
 * day" (anchored to shift start, NOT necessarily calendar midnight — see
 * WorkHoursCalculationService for the day-boundary logic).
 *
 * This row is recomputable: it can always be safely rebuilt from
 * AttendancePunch + AttendanceCorrection. It is NOT a second source of
 * truth — if you ever need to "fix" attendance, you fix it via a
 * correction request, then recompute this row. Never hand-edit this entity.
 */
@Entity
@Table(
        name = "daily_work_record",
        uniqueConstraints = @UniqueConstraint(name = "uk_employee_work_date", columnNames = {"employee_id", "work_date"}),
        indexes = @Index(name = "idx_dwr_employee_date", columnList = "employee_id, work_date")
)
public class DailyWorkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /** The attendance day this record covers — anchored to shift start date,
     * so a night shift starting 23:00 on June 17 is attributed to June 17
     * even though most of the worked minutes fall on June 18. */
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "shift_id")
    private Long shiftId;

    @Column(name = "first_punch_in")
    private LocalDateTime firstPunchIn;

    @Column(name = "last_punch_out")
    private LocalDateTime lastPunchOut;

    @Column(name = "total_worked_minutes", nullable = false)
    private int totalWorkedMinutes;

    @Column(name = "overtime_minutes", nullable = false)
    private int overtimeMinutes;

    @Column(name = "punch_pair_count", nullable = false)
    private int punchPairCount; // how many IN/OUT pairs contributed (e.g. 2 if lunch break split it)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DailyWorkRecordStatus status;

    /** Set when status = CORRECTED; points to the correction that overrode raw computation. */
    @Column(name = "applied_correction_id")
    private Long appliedCorrectionId;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    /** Optimistic locking — recomputation can race with itself if punches and
     * correction-approval events fire close together for the same day. */
    @Version
    @Column(name = "version")
    private Long version;

    protected DailyWorkRecord() {
        // JPA
    }

    public DailyWorkRecord(Long employeeId, LocalDate workDate, Long shiftId,
                           LocalDateTime firstPunchIn, LocalDateTime lastPunchOut,
                           int totalWorkedMinutes, int overtimeMinutes, int punchPairCount,
                           DailyWorkRecordStatus status, Long appliedCorrectionId,
                           LocalDateTime computedAt) {
        this.employeeId = employeeId;
        this.workDate = workDate;
        this.shiftId = shiftId;
        this.firstPunchIn = firstPunchIn;
        this.lastPunchOut = lastPunchOut;
        this.totalWorkedMinutes = totalWorkedMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.punchPairCount = punchPairCount;
        this.status = status;
        this.appliedCorrectionId = appliedCorrectionId;
        this.computedAt = computedAt;
    }

    // Mutators kept narrow and intention-revealing rather than generic setters,
    // since this entity should only ever be touched by the calculation service.

    public void applyComputation(Long shiftId, LocalDateTime firstPunchIn, LocalDateTime lastPunchOut,
                                 int totalWorkedMinutes, int overtimeMinutes, int punchPairCount,
                                 DailyWorkRecordStatus status, Long appliedCorrectionId,
                                 LocalDateTime computedAt) {
        this.shiftId = shiftId;
        this.firstPunchIn = firstPunchIn;
        this.lastPunchOut = lastPunchOut;
        this.totalWorkedMinutes = totalWorkedMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.punchPairCount = punchPairCount;
        this.status = status;
        this.appliedCorrectionId = appliedCorrectionId;
        this.computedAt = computedAt;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDate getWorkDate() { return workDate; }
    public Long getShiftId() { return shiftId; }
    public LocalDateTime getFirstPunchIn() { return firstPunchIn; }
    public LocalDateTime getLastPunchOut() { return lastPunchOut; }
    public int getTotalWorkedMinutes() { return totalWorkedMinutes; }
    public int getOvertimeMinutes() { return overtimeMinutes; }
    public int getPunchPairCount() { return punchPairCount; }
    public DailyWorkRecordStatus getStatus() { return status; }
    public Long getAppliedCorrectionId() { return appliedCorrectionId; }
    public LocalDateTime getComputedAt() { return computedAt; }
    public Long getVersion() { return version; }
}
