package com.people.hub.leavemanagement.controller;

import com.people.hub.leavemanagement.service.LeaveActionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LeaveActionHistoryController {
    private final LeaveActionHistoryService leaveActionHistoryService;
}
