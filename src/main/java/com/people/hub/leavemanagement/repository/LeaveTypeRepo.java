package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.enums.LeaveTypeName;
import com.people.hub.leavemanagement.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepo extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByLeaveTypeName(LeaveTypeName leaveType);

    List<LeaveType> findByActiveTrue();

    boolean existsByLeaveTypeName(LeaveTypeName leaveTypeName);

    boolean existsByLeaveTypeNameAndActiveTrue(
            LeaveTypeName leaveTypeName);
}
