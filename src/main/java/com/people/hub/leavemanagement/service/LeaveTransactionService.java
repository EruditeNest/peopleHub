package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.LeaveTransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveTransactionService {
    private final LeaveTransactionRepo leaveTransactionRepo;
}
