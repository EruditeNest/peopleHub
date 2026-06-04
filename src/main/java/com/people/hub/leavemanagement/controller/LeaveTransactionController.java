package com.people.hub.leavemanagement.controller;

import com.people.hub.leavemanagement.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LeaveTransactionController {
    private final LeaveTransactionService leaveTransactionService;
}
