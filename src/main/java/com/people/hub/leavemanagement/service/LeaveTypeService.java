package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.leavemanagement.dto.LeaveTypeDto;
import com.people.hub.leavemanagement.enums.LeaveTypeName;
import com.people.hub.leavemanagement.model.LeaveType;
import com.people.hub.leavemanagement.repository.LeaveTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {
    private final LeaveTypeRepo leaveTypeRepo;

    public LeaveType createLeaveType(LeaveTypeDto leaveTypeDto){
        LeaveType leaveType = new LeaveType();
        leaveType.setLeaveTypeName(leaveTypeDto.getLeaveTypeName());
        leaveType.setAnnualAllocation(leaveTypeDto.getAnnualAllocation());
        leaveType.setMaxCarryForward(leaveTypeDto.getMaxCarryForward());
        leaveType.setCarryForwardAllowed(leaveTypeDto.getCarryForwardAllowed());
        leaveType.setActive(leaveTypeDto.getActive());
        return leaveTypeRepo.save(leaveType);
    }

    public LeaveType updateLeaveType(Long leaveTypeId, LeaveTypeDto leaveTypeDto) {
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        leaveType.setLeaveTypeName(leaveTypeDto.getLeaveTypeName());
        leaveType.setAnnualAllocation(leaveTypeDto.getAnnualAllocation());
        leaveType.setMaxCarryForward(leaveTypeDto.getMaxCarryForward());
        leaveType.setCarryForwardAllowed(leaveTypeDto.getCarryForwardAllowed());
        leaveType.setActive(leaveTypeDto.getActive());
        return leaveTypeRepo.save(leaveType);
    }

    public LeaveType getLeaveTypeById(Long leaveTypeId) {
        return leaveTypeRepo.findById(leaveTypeId)
                .orElseThrow(() -> new NotFoundException("LeaveType not found"));
    }

    public LeaveType getLeaveType(LeaveTypeName leaveTypeName) {
        return leaveTypeRepo.findByLeaveTypeName(leaveTypeName)
                .orElseThrow(() -> new NotFoundException("LeaveType not found", leaveTypeName.name()));
    }

    public RestApiResponse getAllLeaveTypes() {
        List<LeaveType> leaveTypeList = leaveTypeRepo.findAll();
        return RestApiResponse.success(leaveTypeList);
    }

    public RestApiResponse getActiveLeaveTypes() {
        List<LeaveType> leaveTypeList = leaveTypeRepo.findByActiveTrue();
        return RestApiResponse.success(leaveTypeList);
    }

    public LeaveType activateLeaveType(Long leaveTypeId) {
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        leaveType.setActive(true);
        return leaveTypeRepo.save(leaveType);
    }

    public LeaveType deactivateLeaveType(Long leaveTypeId) {
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        leaveType.setActive(false);
        return leaveTypeRepo.save(leaveType);
    }

    public RestApiResponse deleteLeaveType(Long leaveTypeId) {
        leaveTypeRepo.deleteById(leaveTypeId);
        return RestApiResponse.success("LeaveType deleted successfully");
    }

    public boolean exists(LeaveTypeName leaveTypeName) {
        return leaveTypeRepo.existsByLeaveTypeName(leaveTypeName);
    }

    public boolean isActive(Long leaveTypeId) {
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        return leaveType.getActive();
    }

    public float getAnnualAllocation(Long leaveTypeId) {
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        return  leaveType.getAnnualAllocation();
    }

    public boolean isCarryForwardAllowed(Long leaveTypeId){
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        return  leaveType.getCarryForwardAllowed();
    }

    public float getMaxCarryForward(Long leaveTypeId){
        LeaveType leaveType = getLeaveTypeById(leaveTypeId);
        return  leaveType.getMaxCarryForward();
    }

    public RestApiResponse getCarryForwardEnabledLeaveTypes() {
        List<LeaveType> leaveTypeList = leaveTypeRepo.findAll();
        leaveTypeList = leaveTypeList.stream()
                .filter(LeaveType::getCarryForwardAllowed)
                .toList();
        return RestApiResponse.success(leaveTypeList);
    }

    public RestApiResponse getAllocatableLeaveTypes() {
        List<LeaveType> leaveTypeList = leaveTypeRepo.findAll();
        leaveTypeList = leaveTypeList.stream()
                .filter(LeaveType::getActive)
                .toList();
        return RestApiResponse.success(leaveTypeList);
    }
}
