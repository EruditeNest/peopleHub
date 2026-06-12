package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.enums.LeaveTransactionType;
import com.people.hub.leavemanagement.model.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTransactionRepo extends JpaRepository<LeaveTransaction, Long> {

    List<LeaveTransaction> findByEmployeeId(Long employeeId);

    List<LeaveTransaction> findByLeaveApplicationId(
            Long leaveApplicationId);

    List<LeaveTransaction> findByLeaveTransactionType(LeaveTransactionType transactionType);

    List<LeaveTransaction> findByEmployeeIdAndLeaveTypeId(
            Long employeeId,
            Long leaveTypeId);

    List<LeaveTransaction> findByEmployeeIdAndLeaveTypeIdAndLeaveTransactionType(
            Long employeeId,
            Long leaveTypeId,
            LeaveTransactionType transactionType);

    List<LeaveTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<LeaveTransaction> findTopByEmployeeIdAndLeaveTypeIdOrderByTransactionDateDesc(
            Long employeeId,
            Long leaveTypeId);

    Integer countByEmployeeId(Long employeeId);

    Boolean existsByLeaveApplicationId(Long leaveApplicationId);

    @Query("""
        SELECT COALESCE(SUM(t.days), 0)
        FROM LeaveTransaction t
        WHERE t.employeeId = :employeeId
        AND t.leaveTypeId = :leaveTypeId
        AND t.leaveTransactionType = :transactionType
    """)
    Float getTotalDaysByTransactionType(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("transactionType") LeaveTransactionType transactionType);
}
