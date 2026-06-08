package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepo extends JpaRepository<Holiday, Long> {

    Page<Holiday> findAllHolidayByYear(Integer year, Pageable pageable);

    Page<Holiday> findAllByHolidayDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByHolidayDate(LocalDate holidayDate);

    long countByHolidayDateBetween(LocalDate startDate, LocalDate endDate);

    long countByHolidayDateBetweenAndOptionalHoliday(
            LocalDate startDate,
            LocalDate endDate,
            Boolean optionalHoliday);

    @Query("""
        SELECT h.holidayDate
        FROM Holiday h
        WHERE h.holidayDate BETWEEN :startDate AND :endDate
        ORDER BY h.holidayDate
    """)
    List<LocalDate> findHolidayDatesBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
