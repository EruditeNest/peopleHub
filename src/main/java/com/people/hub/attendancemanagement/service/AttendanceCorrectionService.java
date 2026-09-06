package com.people.hub.attendancemanagement.service;

import com.people.hub.attendancemanagement.dto.CreateCorrectionRequest;
import com.people.hub.attendancemanagement.model.AttendanceCorrection;
import com.people.hub.attendancemanagement.enums.CorrectionStatus;
import com.people.hub.attendancemanagement.exception.DuplicateCorrectionException;
import com.people.hub.attendancemanagement.repository.AttendanceCorrectionRepository;
import com.people.hub.common.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Owns the correction request lifecycle: file -> approve/reject/withdraw.
 *
 * Critically, this service NEVER touches AttendancePunch or computes hours
 * itself — on approval it delegates to WorkHoursCalculationService to
 * recompute the affected day, so there's exactly one place (that service)
 * that decides how a correction's fields translate into worked minutes.
 */
@Service
public class AttendanceCorrectionService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceCorrectionService.class);

    private final AttendanceCorrectionRepository correctionRepository;
    private final WorkHoursCalculationService calculationService;

    public AttendanceCorrectionService(AttendanceCorrectionRepository correctionRepository,
                                       WorkHoursCalculationService calculationService) {
        this.correctionRepository = correctionRepository;
        this.calculationService = calculationService;
    }

    /**
     * Files a new correction request. Rejects outright if a PENDING request
     * already exists for the same (employeeId, workDate) — the requester
     * must withdraw or wait for that one to be decided first, rather than
     * letting two open requests potentially get approved out of order.
     */
    @Transactional
    public AttendanceCorrection fileRequest(CreateCorrectionRequest request, Long requestedByEmployeeId) {
        boolean duplicatePending = correctionRepository.existsByEmployeeIdAndWorkDateAndStatus(
                request.getEmployeeId(), request.getWorkDate(), CorrectionStatus.PENDING);

        if (duplicatePending) {
            throw new DuplicateCorrectionException(
                    "A pending correction already exists for employee " + request.getEmployeeId()
                            + " on " + request.getWorkDate() + ". Withdraw it before filing a new one.");
        }

        AttendanceCorrection correction = new AttendanceCorrection(
                request.getEmployeeId(),
                request.getWorkDate(),
                request.getCorrectionType(),
                request.getRequestedInTime(),
                request.getRequestedOutTime(),
                request.getRequestedWorkedMinutes(),
                request.getReason(),
                request.getSupportingDocumentRef(),
                requestedByEmployeeId,
                LocalDateTime.now()
        );

        AttendanceCorrection saved = correctionRepository.save(correction);
        log.info("Filed correction request id={} employee={} date={} type={}",
                saved.getId(), saved.getEmployeeId(), saved.getWorkDate(), saved.getCorrectionType());
        return saved;
    }

    /**
     * Approves a pending request and immediately triggers recalculation of
     * that day's DailyWorkRecord so the effect is visible right away rather
     * than waiting for the next punch event or a batch job.
     */
    @Transactional
    public AttendanceCorrection approve(Long correctionId, Long decidedByEmployeeId, String decisionNote) {
        AttendanceCorrection correction = getOrThrow(correctionId);
        correction.approve(decidedByEmployeeId, LocalDateTime.now(), decisionNote);
        AttendanceCorrection saved = correctionRepository.save(correction);

        calculationService.recalculate(saved.getEmployeeId(), saved.getWorkDate());

        log.info("Approved correction id={} by={} -> recalculated work record for employee={} date={}",
                saved.getId(), decidedByEmployeeId, saved.getEmployeeId(), saved.getWorkDate());
        return saved;
    }

    @Transactional
    public AttendanceCorrection reject(Long correctionId, Long decidedByEmployeeId, String decisionNote) {
        AttendanceCorrection correction = getOrThrow(correctionId);
        correction.reject(decidedByEmployeeId, LocalDateTime.now(), decisionNote);
        AttendanceCorrection saved = correctionRepository.save(correction);
        log.info("Rejected correction id={} by={}", saved.getId(), decidedByEmployeeId);
        // No recalculation needed — rejection means raw-punch computation
        // (or the prior state) continues to stand.
        return saved;
    }

    /** Requester withdraws their own pending request, e.g. they realize it
     * was filed in error or no longer needed. */
    @Transactional
    public AttendanceCorrection withdraw(Long correctionId) {
        AttendanceCorrection correction = getOrThrow(correctionId);
        correction.withdraw(LocalDateTime.now());
        return correctionRepository.save(correction);
    }

    @Transactional(readOnly = true)
    public List<AttendanceCorrection> getPendingQueue() {
        return correctionRepository.findByStatusOrderByRequestedAtAsc(CorrectionStatus.PENDING);
    }

    @Transactional(readOnly = true)
    public List<AttendanceCorrection> getHistoryForEmployee(Long employeeId) {
        return correctionRepository.findByEmployeeIdOrderByRequestedAtDesc(employeeId);
    }

    private AttendanceCorrection getOrThrow(Long correctionId) {
        return correctionRepository.findById(correctionId)
                .orElseThrow(() -> new NotFoundException("No correction request with id ", correctionId));
    }
}