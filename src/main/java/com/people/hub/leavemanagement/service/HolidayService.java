package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.HolidayRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepo holidayRepo;
}
