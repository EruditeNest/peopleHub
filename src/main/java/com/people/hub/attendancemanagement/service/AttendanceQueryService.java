package com.people.hub.attendancemanagement.service;

import com.people.hub.attendancemanagement.dto.PunchResponse;
import com.people.hub.attendancemanagement.model.AttendancePunch;
import com.people.hub.attendancemanagement.repository.AttendancePunchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Application-facing read access to attendance data — what your Angular/React
 * dashboard, mobile app, or reports module actually calls. Kept separate from
 * AttendanceIngestionService because the access patterns, auth requirements
 * (this needs normal JWT/session auth; the device endpoint cannot have that),
 * and rate of change are completely different.
 */
@Service
@Transactional(readOnly = true)
public class AttendanceQueryService {

    private final AttendancePunchRepository punchRepository;

    public AttendanceQueryService(AttendancePunchRepository punchRepository) {
        this.punchRepository = punchRepository;
    }

    public List<PunchResponse> getPunchesForEmployeeOnDate(Long employeeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        return punchRepository
                .findPunchesForEmployeeOnDay(employeeId, startOfDay, startOfNextDay)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PunchResponse> getUnresolvedPunches() {
        return punchRepository.findByResolvedFalseOrderByReceivedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PunchResponse toResponse(AttendancePunch p) {
        return new PunchResponse(
                p.getId(),
                p.getEmployeeId(),
                p.getPunchTimestamp(),
                p.getDirection(),
                p.getVerifyMode(),
                p.getDeviceSerial(),
                p.isResolved()
        );
    }
}
