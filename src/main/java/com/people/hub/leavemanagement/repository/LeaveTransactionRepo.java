package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTransactionRepo extends JpaRepository<LeaveTransaction, Long> {
}
