package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.leavemanagement.dto.HolidayDto;
import com.people.hub.leavemanagement.model.Holiday;
import com.people.hub.leavemanagement.repository.HolidayRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "holidayDate",
            "holidayName",
            "optionalHoliday",
            "created_at",
            "updated_at"
    );

    private final HolidayRepo holidayRepo;

    public Holiday createHoliday(HolidayDto holidayDto){
        Holiday holiday = new Holiday();
        holiday.setHolidayDate(holidayDto.getHolidayDate());
        holiday.setYear(holidayDto.getYear());
        holiday.setHolidayName(holidayDto.getHolidayName());
        holiday.setOptionalHoliday(holidayDto.getOptionalHoliday());
        return holidayRepo.save(holiday);
    }

    public Holiday updateHoliday(Long holidayId, HolidayDto holidayDto){
        Holiday holiday = getHolidayById(holidayId);
        holiday.setHolidayDate(holidayDto.getHolidayDate());
        holiday.setYear(holidayDto.getYear());
        holiday.setHolidayName(holiday.getHolidayName());
        holiday.setOptionalHoliday(holidayDto.getOptionalHoliday());
        return holidayRepo.save(holiday);
    }

    public Holiday getHolidayById(Long holidayId) {
        return holidayRepo.findById(holidayId)
                .orElseThrow(() -> new NotFoundException("Holiday not found"));
    }

    public RestApiResponse getAllHolidays(int page, int size, String sortField, String sortOrder){
        log.info("getAllHolidays with page: {}, size: {}, sortField: {}, sortOrder: {}",
                page,
                size,
                sortField,
                sortOrder
        );
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS
        );
        Page<Holiday> holidays = holidayRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(holidays.getNumber(), holidays.getSize(), holidays.getTotalElements());
        return RestApiResponse.success(pageInfo, holidays.getContent());
    }

    public RestApiResponse getHolidaysByYear(Integer year, int page, int size, String sortField, String sortOrder){
        log.info("getHolidaysByYear with page: {}, size: {}, sortField: {}, sortOrder: {}",
                page,
                size,
                sortField,
                sortOrder
        );
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS
        );
        Page<Holiday> holidays = holidayRepo.findAllHolidayByYear(year, pageable);
        PageInfo pageInfo = new PageInfo(holidays.getNumber(), holidays.getSize(), holidays.getTotalElements());
        return RestApiResponse.success(pageInfo, holidays.getContent());
    }

    public RestApiResponse getHolidaysBetween(
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortField,
            String sortOrder
    ) {
        log.info("getHolidaysBetween with page: {}, size: {}, sortField: {}, sortOrder: {}",
                page,
                size,
                sortField,
                sortOrder
        );
        Pageable pageable = PageableUtils.getPageable(
                page,
                size,
                sortField,
                sortOrder,
                ALLOWED_SORT_FIELDS
        );
        Page<Holiday> holidays = holidayRepo.findAllByHolidayDateBetween(startDate, endDate, pageable);
        PageInfo pageInfo = new PageInfo(holidays.getNumber(), holidays.getSize(), holidays.getTotalElements());
        return RestApiResponse.success(pageInfo, holidays.getContent());
    }

    public RestApiResponse deleteHoliday(Long holidayId) {
        holidayRepo.deleteById(holidayId);
        return RestApiResponse.success("Holiday deleted successfully");
    }

    public Boolean isHoliday(LocalDate date) {
        return holidayRepo.existsByHolidayDate(date);
    }

    public Long countHolidaysBetween(
            LocalDate startDate,
            LocalDate endDate) {
        return holidayRepo.countByHolidayDateBetween(startDate, endDate);
    }

    public RestApiResponse getHolidayDatesBetween(
            LocalDate startDate,
            LocalDate endDate) {
        List<LocalDate> datesBetween = holidayRepo.findHolidayDatesBetween(startDate, endDate);
        return RestApiResponse.success(datesBetween);
    }

//    BigDecimal calculateWorkingDays(
//            LocalDate startDate,
//            LocalDate endDate){
//
//    }
//
//    BigDecimal calculateLeaveDays(
//            LocalDate startDate,
//            LocalDate endDate){
//
//    }
}
