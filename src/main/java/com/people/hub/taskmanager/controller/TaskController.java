package com.people.hub.taskmanager.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.security.MyUserDetail;
import com.people.hub.taskmanager.dto.ChecklistDto;
import com.people.hub.taskmanager.dto.TaskDto;
import com.people.hub.taskmanager.dto.TimeLogDto;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.Task;
import com.people.hub.taskmanager.model.TaskChecklist;
import com.people.hub.taskmanager.model.TaskTimeLog;
import com.people.hub.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    // CRUD
    @PostMapping("/create")
    public ResponseEntity<Task> createTask(
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal MyUserDetail userDetail) {

        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskDto, userDetail.getUserId()));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(taskService.getTasks(page, size, sortField, sortOrder));
    }

    @PostMapping("/update/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody TaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, taskDto));
    }

    @PostMapping("/delete/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId) {

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // STATUS
    @PostMapping("/{taskId}/status")
    public ResponseEntity<Task> changeStatus(
            @PathVariable Long taskId,
            @RequestParam StatusEnum status) {

        return ResponseEntity.ok(
                taskService.changeStatus(taskId, status));
    }

    // ASSIGNMENT
    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<RestApiResponse> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignTask(taskId, userId));
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<RestApiResponse> assignTaskByMemberIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> userIds) {
        return ResponseEntity.ok(taskService.assignTaskByMemberIds(taskId, userIds));
    }

    @PostMapping("/{taskId}/assign/{memberId}")
    public ResponseEntity<RestApiResponse> removeAssigneeByMemberId(
            @PathVariable Long taskId,
            @PathVariable Long memberId){
        return ResponseEntity.ok(taskService.removeAssignee(taskId, memberId));
    }

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<RestApiResponse> removeAssigneeByMemberIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> memberIds){
        return ResponseEntity.ok(taskService.removeAssigneeByMemberIds(taskId, memberIds));
    }

    // CHECKLIST
    @PostMapping("/checklists/add")
    public ResponseEntity<TaskChecklist> addChecklist(
            @RequestBody ChecklistDto checklistDto,
            @AuthenticationPrincipal MyUserDetail userDetail) {

        return ResponseEntity.ok(taskService.addChecklist(checklistDto, userDetail.getUserId()));
    }

    @PostMapping("/checklists/update/{checklistId}")
    public ResponseEntity<TaskChecklist> updateChecklist(
            @PathVariable Long checklistId,
            @RequestBody ChecklistDto checklistDto,
            @AuthenticationPrincipal MyUserDetail userDetail) {

        return ResponseEntity.ok(taskService.updateChecklist(checklistId, checklistDto, userDetail.getUserId()));
    }

    @PostMapping("/checklists/complete/{checklistId}")
    public ResponseEntity<RestApiResponse> markChecklistComplete(
            @PathVariable Long checklistId,
            @AuthenticationPrincipal MyUserDetail userDetail) {

        return ResponseEntity.ok(taskService.markChecklistComplete(checklistId, userDetail.getUserId()));
    }

    @PostMapping("/checklists/un-complete/{checklistId}")
    public ResponseEntity<RestApiResponse> markChecklistUncomplete(
            @PathVariable Long checklistId) {

        return ResponseEntity.ok(taskService.markChecklistUncomplete(checklistId));
    }

    @PostMapping("/checklists/delete/{checklistId}")
    public ResponseEntity<RestApiResponse> deleteChecklist(
            @PathVariable Long checklistId) {

        return ResponseEntity.ok(taskService.deleteChecklist(checklistId));
    }

    @GetMapping("/{taskId}/checklists")
    public ResponseEntity<List<TaskChecklist>> getChecklists(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                taskService.getChecklists(taskId));
    }

    // FOLLOWERS
    @PostMapping("/{taskId}/followers/add/{userId}")
    public ResponseEntity<RestApiResponse> addFollower(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.addFollower(taskId, userId));
    }

    @PostMapping("/{taskId}/followers/remove/{userId}")
    public ResponseEntity<RestApiResponse> removeFollower(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.removeFollower(taskId, userId));
    }
    @PostMapping("/{taskId}/followers/add")
    public ResponseEntity<RestApiResponse> addFollowerByUserIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> userIds) {
        return ResponseEntity.ok(taskService.addFollowerByUserIds(taskId, userIds));
    }

    @PostMapping("/{taskId}/followers/remove")
    public ResponseEntity<RestApiResponse> removeFollowerByUserIds(
            @PathVariable Long taskId,
            @RequestBody List<Long> userIds) {
        return ResponseEntity.ok(taskService.removeFollowerByUserIds(taskId, userIds));
    }

    @GetMapping("/{taskId}/followers")
    public ResponseEntity<RestApiResponse> getFollowers(@PathVariable Long taskId) {

        return ResponseEntity.ok(taskService.getFollowers(taskId));
    }

    // TIME LOGS
    @PostMapping("/time-logs")
    public ResponseEntity<TaskTimeLog> addTimeLog(@RequestBody TimeLogDto timeLogDto) {

        return ResponseEntity.ok(taskService.addTimeLog(timeLogDto));
    }

    @GetMapping("/{taskId}/time-logs")
    public ResponseEntity<List<TaskTimeLog>> getTimeLogsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTimeLogsByTaskId(taskId));
    }

    @GetMapping("/{taskId}/time-logs/{userId}")
    public ResponseEntity<List<TaskTimeLog>> getTimeLogsByTaskIdAndUserId(@PathVariable Long taskId, @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTimeLogsByTaskIdAndUserId(taskId, userId));
    }

    @PostMapping("/time-logs/delete/{timeLogId}")
    public ResponseEntity<RestApiResponse> deleteTimeLog(@PathVariable Long timeLogId) {
        return ResponseEntity.ok(taskService.deleteTimeLog(timeLogId));
    }
}
