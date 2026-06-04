package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Long> {
}
