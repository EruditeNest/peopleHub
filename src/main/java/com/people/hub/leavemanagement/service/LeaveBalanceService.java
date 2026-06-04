package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.LeaveBalanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {
    private final LeaveBalanceRepo leaveBalanceRepo;
}
