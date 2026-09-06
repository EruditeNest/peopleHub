package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.EmployeeShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment, Long> {

    /**
     * Finds whichever shift assignment was effective for the employee on the
     * given date — i.e. effectiveFrom <= date AND (effectiveTo IS NULL OR effectiveTo >= date).
     * Used by work-hours calc to know which Shift's day-boundary/standard-hours
     * rules applied on that specific historical date, not just "current" shift.
     */
    @Query("SELECT a FROM EmployeeShiftAssignment a WHERE a.employeeId = :employeeId " +
            "AND a.effectiveFrom <= :date " +
            "AND (a.effectiveTo IS NULL OR a.effectiveTo >= :date) " +
            "ORDER BY a.effectiveFrom DESC")
    Optional<EmployeeShiftAssignment> findEffectiveAssignment(
            @Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}

