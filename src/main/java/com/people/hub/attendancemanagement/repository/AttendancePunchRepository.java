package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.AttendancePunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendancePunchRepository extends JpaRepository<AttendancePunch, Long> {

    /**
     * Used for idempotency checks before insert. The (deviceSerial, devicePin,
     * timestamp, sequence) tuple matches the DB unique constraint, so this
     * lookup (or relying on a caught constraint-violation) is how duplicate
     * pushes from the device's retry behavior get silently absorbed.
     */
    Optional<AttendancePunch> findByDeviceSerialAndDeviceEmployeePinAndPunchTimestampAndDeviceSequence(
            String deviceSerial, String deviceEmployeePin, LocalDateTime punchTimestamp, Integer deviceSequence);

    List<AttendancePunch> findByEmployeeIdAndPunchTimestampBetweenOrderByPunchTimestampAsc(
            Long employeeId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT p FROM AttendancePunch p WHERE p.employeeId = :employeeId " +
           "AND p.punchTimestamp >= :from AND p.punchTimestamp < :to " +
           "ORDER BY p.punchTimestamp ASC")
    List<AttendancePunch> findPunchesForEmployeeOnDay(
            @Param("employeeId") Long employeeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /** Punches that failed PIN→employee resolution, for an admin reconciliation screen. */
    List<AttendancePunch> findByResolvedFalseOrderByReceivedAtDesc();

    List<AttendancePunch> findByDeviceSerialAndReceivedAtAfterOrderByReceivedAtDesc(
            String deviceSerial, LocalDateTime after);
}
