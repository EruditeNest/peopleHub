package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.repository.LeaveTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepo leaveTypeRepo;
}
