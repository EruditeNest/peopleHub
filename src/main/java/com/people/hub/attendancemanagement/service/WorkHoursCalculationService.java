package com.people.hub.attendancemanagement.service;

import com.people.hub.attendancemanagement.dto.PunchPairingResult;
import com.people.hub.attendancemanagement.model.*;
import com.people.hub.attendancemanagement.enums.CorrectionStatus;
import com.people.hub.attendancemanagement.enums.CorrectionType;
import com.people.hub.attendancemanagement.enums.DailyWorkRecordStatus;
import com.people.hub.attendancemanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Computes/recomputes DailyWorkRecord for one employee for one attendance
 * day. This is the single place that decides "how many minutes did this
 * person work today" — both the listener reacting to new punches and the
 * correction-approval flow funnel through here, so there is exactly one
 * code path that can disagree with itself.
 *
 * "Attendance day" boundary: anchored to the employee's shift START time,
 * not calendar midnight. For a night shift (e.g. 23:00 -> 07:00), a punch at
 * 00:30 logically belongs to the shift that started the PREVIOUS calendar
 * day at 23:00. Getting this right is what makes night-shift overtime/work-
 * hours numbers correct instead of split nonsensically across two days.
 */
@Service
public class WorkHoursCalculationService {

    private static final Logger log = LoggerFactory.getLogger(WorkHoursCalculationService.class);

    /** Used when an employee has no shift assignment on record — falls back
     * to plain calendar-day boundaries and a generic 8-hour standard. Flag
     * this in monitoring; every employee should have a real assignment. */
    private static final int DEFAULT_STANDARD_MINUTES = 480;

    private final AttendancePunchRepository punchRepository;
    private final DailyWorkRecordRepository workRecordRepository;
    private final AttendanceCorrectionRepository correctionRepository;
    private final EmployeeShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftRepository shiftRepository;
    private final PunchPairingAlgorithm pairingAlgorithm;

    public WorkHoursCalculationService(AttendancePunchRepository punchRepository,
                                       DailyWorkRecordRepository workRecordRepository,
                                       AttendanceCorrectionRepository correctionRepository,
                                       EmployeeShiftAssignmentRepository shiftAssignmentRepository,
                                       ShiftRepository shiftRepository,
                                       PunchPairingAlgorithm pairingAlgorithm) {
        this.punchRepository = punchRepository;
        this.workRecordRepository = workRecordRepository;
        this.correctionRepository = correctionRepository;
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftRepository = shiftRepository;
        this.pairingAlgorithm = pairingAlgorithm;
    }

    /**
     * Recomputes (or creates) the DailyWorkRecord for one employee/day.
     * Idempotent and safe to call repeatedly — e.g. once per new punch event,
     * and again whenever a correction is approved for that day.
     */
    @Transactional
    public DailyWorkRecord recalculate(Long employeeId, LocalDate workDate) {
        Shift shift = resolveShift(employeeId, workDate);
        DailyWorkRecord record = workRecordRepository.findByEmployeeIdAndWorkDate(employeeId, workDate)
                .orElseGet(() -> new DailyWorkRecord(
                        employeeId, workDate, shift != null ? shift.getId() : null,
                        null, null, 0, 0, 0, DailyWorkRecordStatus.NO_PUNCHES, null, LocalDateTime.now()));

        // An approved correction always wins over raw-punch computation —
        // this is the ONLY place that precedence is decided, so there's no
        // risk of some other code path silently preferring raw punches.
        Optional<AttendanceCorrection> approvedCorrection = correctionRepository
                .findByEmployeeIdAndWorkDateAndStatusOrderByDecidedAtDesc(
                        employeeId, workDate, CorrectionStatus.APPROVED);

        if (approvedCorrection.isPresent()) {
            applyCorrection(record, approvedCorrection.get(), shift);
        } else {
            applyRawPunchComputation(record, employeeId, workDate, shift);
        }

        DailyWorkRecord saved = workRecordRepository.save(record);
        log.info("Recalculated work record: employee={} date={} status={} workedMin={} otMin={}",
                employeeId, workDate, saved.getStatus(), saved.getTotalWorkedMinutes(), saved.getOvertimeMinutes());
        return saved;
    }

    private void applyRawPunchComputation(DailyWorkRecord record, Long employeeId,
                                          LocalDate workDate, Shift shift) {
        LocalDateTime windowStart = dayWindowStart(workDate, shift);
        LocalDateTime windowEnd = windowStart.plusHours(24);

        List<AttendancePunch> punches = punchRepository
                .findByEmployeeIdAndPunchTimestampBetweenOrderByPunchTimestampAsc(
                        employeeId, windowStart, windowEnd);

        if (punches.isEmpty()) {
            record.applyComputation(shift != null ? shift.getId() : null,
                    null, null, 0, 0, 0, DailyWorkRecordStatus.NO_PUNCHES, null, LocalDateTime.now());
            return;
        }

        PunchPairingResult pairing = pairingAlgorithm.pair(punches);
        int standardMinutes = shift != null ? shift.getStandardMinutes() : DEFAULT_STANDARD_MINUTES;
        int overtimeMinutes = Math.max(0, pairing.getTotalWorkedMinutes() - standardMinutes);

        DailyWorkRecordStatus status = pairing.isClean()
                ? DailyWorkRecordStatus.COMPUTED
                : DailyWorkRecordStatus.INCOMPLETE;

        record.applyComputation(
                shift != null ? shift.getId() : null,
                pairing.getFirstIn(),
                pairing.getLastOut(),
                pairing.getTotalWorkedMinutes(),
                overtimeMinutes,
                pairing.getPairCount(),
                status,
                null,
                LocalDateTime.now());
    }

    private void applyCorrection(DailyWorkRecord record, AttendanceCorrection correction, Shift shift) {
        int standardMinutes = shift != null ? shift.getStandardMinutes() : DEFAULT_STANDARD_MINUTES;
        int workedMinutes;
        LocalDateTime in;
        LocalDateTime out;

        if (correction.getCorrectionType() == CorrectionType.MANUAL_DAY_OVERRIDE) {
            workedMinutes = correction.getRequestedWorkedMinutes() != null
                    ? correction.getRequestedWorkedMinutes() : 0;
            in = correction.getRequestedInTime();
            out = correction.getRequestedOutTime();
        } else if (correction.getCorrectionType() == CorrectionType.LEAVE_OR_HOLIDAY) {
            // Approved leave/holiday: zero worked minutes, but status is
            // CORRECTED (not NO_PUNCHES/INCOMPLETE) so it's clearly
            // distinguished from an unexplained absence in reports.
            workedMinutes = 0;
            in = null;
            out = null;
        } else {
            // MISSING_PUNCH or WRONG_TIME: requester supplied the actual
            // corrected in/out times directly.
            in = correction.getRequestedInTime();
            out = correction.getRequestedOutTime();
            workedMinutes = (in != null && out != null)
                    ? (int) java.time.Duration.between(in, out).toMinutes()
                    : 0;
        }

        int overtimeMinutes = Math.max(0, workedMinutes - standardMinutes);

        record.applyComputation(
                shift != null ? shift.getId() : null,
                in, out, workedMinutes, overtimeMinutes,
                (in != null && out != null) ? 1 : 0,
                DailyWorkRecordStatus.CORRECTED,
                correction.getId(),
                LocalDateTime.now());
    }

    /** Resolves the shift effective for this employee on this date, or null
     * if unassigned (falls back to calendar-day/default-hours behavior). */
    private Shift resolveShift(Long employeeId, LocalDate workDate) {
        return shiftAssignmentRepository.findEffectiveAssignment(employeeId, workDate)
                .flatMap(assignment -> shiftRepository.findById(assignment.getShiftId()))
                .orElse(null);
    }

    /**
     * The moment the "attendance day" window begins. For a normal day shift
     * this is just midnight of workDate. For a shift that crosses midnight
     * (e.g. 23:00 start), the window is anchored to the shift's actual start
     * time on workDate, so punches up to ~23 hours later still fall inside
     * the SAME attendance day rather than spilling into tomorrow's record.
     */
    private LocalDateTime dayWindowStart(LocalDate workDate, Shift shift) {
        if (shift == null) {
            return workDate.atStartOfDay();
        }
        LocalTime anchor = shift.isCrossesMidnight() ? shift.getStartTime() : LocalTime.MIDNIGHT;
        return LocalDateTime.of(workDate, anchor);
    }
}