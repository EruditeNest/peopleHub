package com.people.hub.taskmanager.controller;

import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.Task;
import com.people.hub.taskmanager.model.TaskChecklist;
import com.people.hub.taskmanager.model.TaskFollower;
import com.people.hub.taskmanager.model.TaskTimeLog;
import com.people.hub.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    // CRUD
    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody CreateTaskRequest request) {

        return ResponseEntity.ok(
                taskService.createTask(request));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                taskService.getTaskById(taskId));
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(
            Pageable pageable) {

        return ResponseEntity.ok(
                taskService.getTasks(pageable));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request) {

        return ResponseEntity.ok(
                taskService.updateTask(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId) {

        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // STATUS
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> changeStatus(
            @PathVariable Long taskId,
            @RequestParam StatusEnum status) {

        return ResponseEntity.ok(
                taskService.changeStatus(taskId, status));
    }

    // ASSIGNMENT
    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                taskService.assignTask(taskId, userId));
    }

    // CHECKLIST
    @PostMapping("/{taskId}/checklists")
    public ResponseEntity<TaskChecklist> addChecklist(
            @PathVariable Long taskId,
            @RequestBody CreateChecklistRequest request) {

        return ResponseEntity.ok(
                taskService.addChecklist(taskId, request));
    }

    @PutMapping("/checklists/{checklistId}")
    public ResponseEntity<TaskChecklist> updateChecklist(
            @PathVariable Long checklistId,
            @RequestBody UpdateChecklistRequest request) {

        return ResponseEntity.ok(
                taskService.updateChecklist(checklistId, request));
    }

    @PatchMapping("/checklists/{checklistId}/complete")
    public ResponseEntity<Void> markChecklistComplete(
            @PathVariable Long checklistId) {

        taskService.markChecklistComplete(checklistId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/checklists/{checklistId}")
    public ResponseEntity<Void> deleteChecklist(
            @PathVariable Long checklistId) {

        taskService.deleteChecklist(checklistId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/checklists")
    public ResponseEntity<List<TaskChecklist>> getChecklists(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                taskService.getChecklists(taskId));
    }

    // FOLLOWERS
    @PostMapping("/{taskId}/followers/{userId}")
    public ResponseEntity<Void> addFollower(
            @PathVariable Long taskId,
            @PathVariable Long userId) {

        taskService.addFollower(taskId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/followers/{userId}")
    public ResponseEntity<Void> removeFollower(
            @PathVariable Long taskId,
            @PathVariable Long userId) {

        taskService.removeFollower(taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/followers")
    public ResponseEntity<List<TaskFollower>> getFollowers(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                taskService.getFollowers(taskId));
    }

    // TIME LOGS
    @PostMapping("/{taskId}/time-logs")
    public ResponseEntity<TaskTimeLog> addTimeLog(
            @PathVariable Long taskId,
            @RequestBody CreateTaskTimeLogRequest request) {

        return ResponseEntity.ok(
                taskService.addTimeLog(taskId, request));
    }

    @GetMapping("/{taskId}/time-logs")
    public ResponseEntity<List<TaskTimeLog>> getTimeLogs(
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                taskService.getTimeLogs(taskId));
    }

    @DeleteMapping("/time-logs/{timeLogId}")
    public ResponseEntity<Void> deleteTimeLog(
            @PathVariable Long timeLogId) {

        taskService.deleteTimeLog(timeLogId);
        return ResponseEntity.noContent().build();
    }
}
