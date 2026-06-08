package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveApplicationRepo extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByEmployeeId(
            Long employeeId);

    Page<LeaveApplication> findAllByEmployeeId(Long employeeId, Pageable pageable);

    List<LeaveApplication> findByEmployeeIdAndStatus(
            Long employeeId,
            LeaveStatus status);

    List<LeaveApplication> findByManagerIdAndStatus(
            Long managerId,
            LeaveStatus status);


    Page<LeaveApplication> findByManagerIdAndStatus(
            Long managerId,
            LeaveStatus status,
            Pageable pageable);

    List<LeaveApplication> findByHrIdAndStatus(
            Long hrId,
            LeaveStatus status);


    Page<LeaveApplication> findByHrIdAndStatus(
            Long hrId,
            LeaveStatus status,
            Pageable pageable);

    List<LeaveApplication> findByAppliedAtBetween(
            LocalDateTime start,
            LocalDateTime end);
}
