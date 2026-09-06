package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
}
