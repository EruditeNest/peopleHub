package com.people.hub.leavemanagement.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.leavemanagement.enums.ActionRole;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveActionHistory;
import com.people.hub.leavemanagement.repository.LeaveActionHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveActionHistoryService {
    private final LeaveActionHistoryRepo actionHistoryRepo;

    public LeaveActionHistory createHistory(
            Long leaveApplicationId,
            Long actionBy,
            ActionRole actionRole,
            LeaveStatus fromStatus,
            LeaveStatus toStatus,
            String remarks){
        LeaveActionHistory leaveActionHistory = new LeaveActionHistory();
        leaveActionHistory.setLeaveApplicationId(leaveApplicationId);
        leaveActionHistory.setActionBy(actionBy);
        leaveActionHistory.setActionRole(actionRole);
        leaveActionHistory.setFromStatus(fromStatus);
        leaveActionHistory.setToStatus(toStatus);
        leaveActionHistory.setRemarks(remarks);
        leaveActionHistory.setActionDate(LocalDateTime.now());
        return actionHistoryRepo.save(leaveActionHistory);
    }

    public RestApiResponse getHistoryByLeaveApplicationId(Long leaveApplicationId){
        List<LeaveActionHistory> leaveActionHistoryList = actionHistoryRepo.findByLeaveApplicationIdOrderByActionDateDesc(leaveApplicationId);
        return RestApiResponse.success(leaveActionHistoryList);
    }

    public LeaveActionHistory getHistoryById(Long historyId){
        return actionHistoryRepo.findById(historyId)
                .orElseThrow(() -> new NotFoundException("LeaveActionHistory not found", historyId));
    }

    public RestApiResponse getHistoryByActionBy(Long actionBy){
        List<LeaveActionHistory> leaveActionHistoryList = actionHistoryRepo.findByActionBy(actionBy);
        return RestApiResponse.success(leaveActionHistoryList);
    }

    public RestApiResponse getHistoryBetweenDates(LocalDateTime startDate, LocalDateTime endDate){
        List<LeaveActionHistory> leaveActionHistoryList = actionHistoryRepo.findByActionDateBetween(startDate, endDate);
        return RestApiResponse.success(leaveActionHistoryList);
    }

    public RestApiResponse getHistoryByStatusTransition(LeaveStatus fromStatus, LeaveStatus toStatus){
        List<LeaveActionHistory> leaveActionHistoryList = actionHistoryRepo.findByFromStatusAndToStatus(fromStatus, toStatus);
        return RestApiResponse.success(leaveActionHistoryList);
    }

    public LeaveActionHistory getLatestAction(Long leaveApplicationId){
        return actionHistoryRepo.findTopByLeaveApplicationIdOrderByActionDateDesc(leaveApplicationId)
                .orElseThrow(() -> new NotFoundException("LeaveActionHistory not found for LeaveApplicationId: ", leaveApplicationId));
    }

    public boolean hasBeenOnHold(Long leaveApplicationId){
        List<LeaveActionHistory> leaveActionHistoryList = actionHistoryRepo.findByLeaveApplicationIdOrderByActionDateDesc(leaveApplicationId);
        for(LeaveActionHistory actionHistory: leaveActionHistoryList) {
            if(actionHistory.getToStatus().equals(LeaveStatus.ON_HOLD)) {
                return true;
            }
        }
        return false;
    }

    public long countActionsByRole(ActionRole actionRole){
        return actionHistoryRepo.countByActionRole(actionRole);
    }
}
