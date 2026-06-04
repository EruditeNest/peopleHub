package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveActionHistoryRepo extends JpaRepository<LeaveActionHistory, Long> {
}
