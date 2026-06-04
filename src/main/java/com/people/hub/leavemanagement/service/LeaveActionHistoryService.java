package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.LeaveActionHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveActionHistoryService {
    private final LeaveActionHistoryRepo leaveActionHistoryRepo;
}
