package com.people.hub.leavemanagement.controller;

import com.people.hub.leavemanagement.service.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LeaveApplicationController {
    private final LeaveApplicationService leaveApplicationService;
}
