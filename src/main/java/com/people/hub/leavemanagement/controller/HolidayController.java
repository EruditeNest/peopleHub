package com.people.hub.leavemanagement.controller;

import com.people.hub.leavemanagement.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;
}
