package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepo extends JpaRepository<LeaveType, Long> {
}
