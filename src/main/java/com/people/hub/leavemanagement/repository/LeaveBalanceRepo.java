package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepo extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndFinancialYear(
            Long employeeId,
            Long leaveTypeId,
            String financialYear);

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByFinancialYear(Integer financialYear);

    List<LeaveBalance> findByEmployeeIdAndFinancialYear(
            Long employeeId,
            String financialYear);

    boolean existsByEmployeeIdAndLeaveTypeIdAndLeaveYear(
            Long employeeId,
            Long leaveTypeId,
            Integer leaveYear);
}
