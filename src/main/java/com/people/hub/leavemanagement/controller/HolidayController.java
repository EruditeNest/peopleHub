package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.dto.HolidayDto;
import com.people.hub.leavemanagement.model.Holiday;
import com.people.hub.leavemanagement.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {
    private final HolidayService holidayService;

    @PostMapping
    public ResponseEntity<Holiday> createHoliday(HolidayDto holidayDto){
        Holiday holiday = holidayService.createHoliday(holidayDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(holiday);
    }

    @PostMapping("/{holidayId}/update")
    public ResponseEntity<Holiday> updateHoliday(
            @PathVariable Long holidayId,
            @RequestBody HolidayDto holidayDto) {
        Holiday holiday = holidayService.updateHoliday(holidayId, holidayDto);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping("/{holidayId}")
    public ResponseEntity<Holiday> getHolidayById(@PathVariable Long holidayId) {
        Holiday holiday = holidayService.getHolidayById(holidayId);
        return ResponseEntity.ok(holiday);
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getAllHolidays(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "created_at") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        RestApiResponse response = holidayService.getAllHolidays(page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/year")
    public ResponseEntity<RestApiResponse> getHolidaysByYear(
            @RequestParam int year,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "created_at") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        RestApiResponse response = holidayService.getHolidaysByYear(year, page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/between")
    public ResponseEntity<RestApiResponse> getHolidaysBetween(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "created_at") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        RestApiResponse response = holidayService.getHolidaysBetween(
                startDate,
                endDate,
                page,
                size,
                sortField,
                sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dates/between")
    public ResponseEntity<RestApiResponse> getHolidayDatesBetween(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        RestApiResponse response = holidayService.getHolidayDatesBetween(
                startDate,
                endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{holidayId}/delete")
    public ResponseEntity<RestApiResponse> deleteHoliday(@PathVariable Long holidayId){
        RestApiResponse response = holidayService.deleteHoliday(holidayId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/is-holiday")
    public ResponseEntity<Boolean> isHoliday(@RequestParam LocalDate date){
        Boolean response = holidayService.isHoliday(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/between")
    public ResponseEntity<Long> countHolidaysBetween(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Long response = holidayService.countHolidaysBetween(
                startDate,
                endDate);
        return ResponseEntity.ok(response);
    }
}
