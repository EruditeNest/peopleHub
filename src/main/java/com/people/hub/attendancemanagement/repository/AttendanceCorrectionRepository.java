package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.AttendanceCorrection;
import com.people.hub.attendancemanagement.enums.CorrectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long> {

    Optional<AttendanceCorrection> findByEmployeeIdAndWorkDateAndStatus(
            Long employeeId, LocalDate workDate, CorrectionStatus status);

    /** The currently-applicable approved correction for a day, if any —
     * used by WorkHoursCalculationService to decide whether to override raw computation. */
    Optional<AttendanceCorrection> findByEmployeeIdAndWorkDateAndStatusOrderByDecidedAtDesc(
            Long employeeId, LocalDate workDate, CorrectionStatus status);

    List<AttendanceCorrection> findByStatusOrderByRequestedAtAsc(CorrectionStatus status);

    List<AttendanceCorrection> findByEmployeeIdOrderByRequestedAtDesc(Long employeeId);

    boolean existsByEmployeeIdAndWorkDateAndStatus(Long employeeId, LocalDate workDate, CorrectionStatus status);
}
