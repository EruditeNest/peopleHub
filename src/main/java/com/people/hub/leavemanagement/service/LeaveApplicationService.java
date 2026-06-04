package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.LeaveApplicationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {
    private final LeaveApplicationRepo leaveApplicationRepo;
}
