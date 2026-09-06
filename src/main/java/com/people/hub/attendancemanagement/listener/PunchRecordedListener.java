package com.people.hub.attendancemanagement.listener;

import com.people.hub.attendancemanagement.service.AttendanceIngestionService.PunchRecordedEvent;
import com.people.hub.attendancemanagement.service.WorkHoursCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

/**
 * Bridges punch ingestion to work-hours recalculation without coupling the
 * two services directly. Listens AFTER_COMMIT so it never fires for a punch
 * that ultimately rolled back, and runs @Async so a slow recalculation never
 * makes the device's HTTP push wait (remember: the device expects a fast
 * "OK" response or it'll consider the push failed and retry).
 *
 * Unresolved punches (employeeId == null, no device-PIN mapping found) are
 * skipped here — there's nothing to recalculate for an unknown employee.
 * They surface instead via AttendanceQueryService.getUnresolvedPunches()
 * for ops to fix the mapping, after which a manual recalculation trigger
 * (or a daily sweep job) should be used to backfill.
 */
@Component
public class PunchRecordedListener {

    private static final Logger log = LoggerFactory.getLogger(PunchRecordedListener.class);

    private final WorkHoursCalculationService calculationService;

    public PunchRecordedListener(WorkHoursCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPunchRecorded(PunchRecordedEvent event) {
        if (event.employeeId() == null) {
            log.debug("Skipping recalculation for unresolved punch id={}", event.punchId());
            return;
        }
        try {
            // Note: this uses calendar date of the punch timestamp as a
            // starting point. For night shifts, WorkHoursCalculationService's
            // own shift-anchored window logic is what actually determines the
            // correct "attendance day" internally — recalculating against
            // the punch's calendar date is safe because the service re-derives
            // the true window from the employee's shift, not from this date alone.
            LocalDate workDate = event.punchTimestamp().toLocalDate();
            calculationService.recalculate(event.employeeId(), workDate);

            // A late-night punch belonging to a shift that STARTED yesterday
            // could also affect yesterday's record if punches arrive out of
            // order; recalculating the previous day too is cheap insurance.
            calculationService.recalculate(event.employeeId(), workDate.minusDays(1));
        } catch (Exception e) {
            log.error("Failed to recalculate work hours for employee={} after punch={}",
                    event.employeeId(), event.punchId(), e);
            // Deliberately swallowed: a recalculation failure must never
            // propagate back and affect the (already-committed) punch
            // ingestion. A scheduled reconciliation sweep should catch any
            // record that's stale relative to its punches.
        }
    }
}