package com.people.hub.leavemanagement.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.leavemanagement.enums.ActionRole;
import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.leavemanagement.model.LeaveActionHistory;
import com.people.hub.leavemanagement.service.LeaveActionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/leave-action-history")
@RequiredArgsConstructor
public class LeaveActionHistoryController {
    private final LeaveActionHistoryService actionHistoryService;

    @GetMapping("/application-id/{leaveApplicationId}")
    public ResponseEntity<RestApiResponse> getHistoryByLeaveApplicationId(@PathVariable Long leaveApplicationId){
        return ResponseEntity.ok(actionHistoryService.getHistoryByLeaveApplicationId(leaveApplicationId));
    }

    @GetMapping("/history-id/{historyId}")
    public ResponseEntity<LeaveActionHistory> getHistoryById(@PathVariable Long historyId){
        return ResponseEntity.ok(actionHistoryService.getHistoryById(historyId));
    }

    @GetMapping("/action-by/{actionBy}")
    public ResponseEntity<RestApiResponse> getHistoryByActionBy(@PathVariable Long actionBy){
        return ResponseEntity.ok(actionHistoryService.getHistoryByActionBy(actionBy));
    }

    @GetMapping("/between-dates")
    public ResponseEntity<RestApiResponse> getHistoryBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate){
        return ResponseEntity.ok(actionHistoryService.getHistoryBetweenDates(startDate, endDate));
    }

    @GetMapping("/status-transition")
    public ResponseEntity<RestApiResponse> getHistoryByStatusTransition(
            @RequestParam LeaveStatus fromStatus,
            @RequestParam LeaveStatus toStatus){
        return ResponseEntity.ok(actionHistoryService.getHistoryByStatusTransition(fromStatus, toStatus));
    }

    @GetMapping("/latest/{leaveApplicationId}")
    public ResponseEntity<LeaveActionHistory> getLatestAction(@PathVariable Long leaveApplicationId){
        return ResponseEntity.ok(actionHistoryService.getLatestAction(leaveApplicationId));
    }

    @GetMapping("/been-on-hold/{leaveApplicationId}")
    public ResponseEntity<Boolean> hasBeenOnHold(@PathVariable Long leaveApplicationId){
        return ResponseEntity.ok(actionHistoryService.hasBeenOnHold(leaveApplicationId));
    }

    @GetMapping("/count/{actionRole}")
    public ResponseEntity<Long> countActionsByRole(@PathVariable ActionRole actionRole){
        return ResponseEntity.ok(actionHistoryService.countActionsByRole(actionRole));
    }
}
