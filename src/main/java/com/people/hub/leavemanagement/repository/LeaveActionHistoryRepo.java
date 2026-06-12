package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.enums.ActionRole;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveActionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveActionHistoryRepo extends JpaRepository<LeaveActionHistory, Long> {

    List<LeaveActionHistory> findByLeaveApplicationIdOrderByActionDateDesc(Long leaveApplicationId);

    List<LeaveActionHistory> findByActionBy(Long actionBy);

    List<LeaveActionHistory> findByActionRole(ActionRole actionRole);

    List<LeaveActionHistory> findByActionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<LeaveActionHistory> findByFromStatusAndToStatus(LeaveStatus fromStatus, LeaveStatus toStatus);

    Optional<LeaveActionHistory> findTopByLeaveApplicationIdOrderByActionDateDesc(Long leaveApplicationId);

    boolean existsByLeaveApplicationIdAndToStatus(Long leaveApplicationId, LeaveStatus status);

    long countByActionRole(ActionRole actionRole);
}
