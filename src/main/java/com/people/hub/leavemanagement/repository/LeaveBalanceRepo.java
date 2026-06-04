package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveBalanceRepo extends JpaRepository<LeaveBalance, Long> {
}
