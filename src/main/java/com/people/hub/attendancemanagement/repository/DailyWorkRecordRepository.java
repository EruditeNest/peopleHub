package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.DailyWorkRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyWorkRecordRepository extends JpaRepository<DailyWorkRecord, Long> {

    Optional<DailyWorkRecord> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<DailyWorkRecord> findByEmployeeIdAndWorkDateBetweenOrderByWorkDateAsc(
            Long employeeId, LocalDate from, LocalDate to);
}

