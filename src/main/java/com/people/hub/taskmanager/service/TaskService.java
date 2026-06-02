package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.service.UserServicePort;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.taskmanager.dto.ChecklistDto;
import com.people.hub.taskmanager.dto.TaskDto;
import com.people.hub.taskmanager.dto.TimeLogDto;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.*;
import com.people.hub.taskmanager.repo.TaskRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id",
        "name",
        "description",
        "status",
        "dueDate",
        "assignedBy",
        "priority",
        "projectId",
        "created_at",
        "updated_at"
    );

    private final TaskRepo taskRepo;
    private final TaskMemberService taskMemberService;
    private final TaskChecklistService taskChecklistService;
    private final TaskTimeLogService taskTimeLogService;
    private final TaskFollowerService taskFollowerService;
    private final UserServicePort userServicePort;

    public Task createTask(TaskDto taskDto, Long userId){
        Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setDueDate(taskDto.getDueDate());
        task.setAssignedBy(userId);
        task.setPriority(taskDto.getPriority());
        task.setProjectId(taskDto.getProjectId());
        task.setRequiredManDays(taskDto.getRequiredManDays());
        return taskRepo.save(task);
    }

    public Task getTaskById(Long taskId){
        return taskRepo.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", taskId));
    }

    public RestApiResponse getAllByProjectId(Long projectId, int page, int size, String sortField, String sortOrder){
        log.info("getProjectTasks with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);

        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Task> projects = taskRepo.findAllByProjectId(projectId, pageable);
        PageInfo pageInfo = new PageInfo(projects.getNumber(), projects.getSize(), projects.getTotalElements());
        return RestApiResponse.success(pageInfo, projects.getContent());
    }

    public RestApiResponse getAllByMemberId(Long memberId, int page, int size, String sortField, String sortOrder){
        log.info("getProjectTasks with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        return RestApiResponse.success();
    }

    public RestApiResponse getTasks(int page, int size, String sortField, String sortOrder){
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Task> tasks = taskRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(tasks.getNumber(), tasks.getSize(), tasks.getTotalElements());
        return RestApiResponse.success(pageInfo, tasks.getContent());
    }

    public Task updateTask(
            Long taskId,
            TaskDto taskDto){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", taskId));
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setDueDate(taskDto.getDueDate());
        task.setAssignedBy(taskDto.getAssignedBy());
        task.setPriority(taskDto.getPriority());
        task.setProjectId(taskDto.getProjectId());
        task.setRequiredManDays(taskDto.getRequiredManDays());
        return taskRepo.save(task);
    }

    public RestApiResponse deleteTask(Long taskId){
        taskRepo.deleteById(taskId);
        return RestApiResponse.success("Task deleted successfully" + taskId);
    }

    public Task changeStatus(
            Long taskId,
            StatusEnum status){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found", taskId));
        task.setStatus(status);
        return taskRepo.save(task);
    }

    public RestApiResponse assignTask(
            Long taskId,
            Long userId){
        if(userServicePort.activeUserExistsById(userId) && taskRepo.existsById(taskId)){
            taskMemberService.assignTaskByMemberId(taskId, userId);
            return RestApiResponse.success("Task assigned successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse assignTaskByMemberIds(
            Long taskId,
            List<Long> memberIds){
        Set<Long> uniqueMemberIds = userServicePort.getAllActiveUserIds(memberIds);
        if(!uniqueMemberIds.isEmpty() && taskRepo.existsById(taskId)){
            taskMemberService.assignTaskByMemberIds(taskId, uniqueMemberIds);
            return RestApiResponse.success("Task assigned successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeAssignee(Long taskId, Long memberId){
        if(userServicePort.activeUserExistsById(memberId) && taskRepo.existsById(taskId)){
            taskMemberService.removeAssigneeByMemberId(taskId, memberId);
            return RestApiResponse.success("Assignee removed successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeAssigneeByMemberIds(Long taskId, List<Long> memberIds){
        Set<Long> uniqueMemberIds = userServicePort.getAllActiveUserIds(memberIds);
        if(!uniqueMemberIds.isEmpty() && taskRepo.existsById(taskId)){
            taskMemberService.removeAssigneeByMemberIds(taskId, uniqueMemberIds);
            return RestApiResponse.success("Assignees removed successfully");
        }
        return RestApiResponse.failure();
    }

    public TaskChecklist addChecklist(ChecklistDto checklistDto, Long userId){
        if(userServicePort.activeUserExistsById(userId) && taskRepo.existsById(checklistDto.getTaskId())){
            return taskChecklistService.addChecklist(checklistDto, userId);
        }
        return null;
    }

    public TaskChecklist updateChecklist(
            Long checklistId,
            ChecklistDto checklistDto,
            Long userId){
        if(userServicePort.activeUserExistsById(userId) && taskRepo.existsById(checklistDto.getTaskId())){
            return taskChecklistService.updateChecklist(checklistId, checklistDto, userId);
        }
        return null;
    }

    public RestApiResponse markChecklistComplete(Long checklistId, Long userId){
        if(userServicePort.activeUserExistsById(userId)) {
            taskChecklistService.markChecklistComplete(checklistId, userId);
            return RestApiResponse.success("Checklist marked completed.");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse markChecklistUncomplete(Long checklistId){
        taskChecklistService.markChecklistUncomplete(checklistId);
        return RestApiResponse.success("Checklist marked Uncompleted.");
    }

    public RestApiResponse deleteChecklist(Long checklistId){
        taskChecklistService.deleteChecklist(checklistId);
        return RestApiResponse.success("Checklist deleted successfully");
    }

    /*
    * TODO: create a checklist response object to carry users based on ids.
     */
    public List<TaskChecklist> getChecklists(Long taskId){
        if(taskRepo.existsById(taskId)) {
            return taskChecklistService.getChecklists(taskId);
        }
        throw new NotFoundException("Task not found", taskId);
    }

    public RestApiResponse addFollower(
            Long taskId,
            Long userId){
        if(taskRepo.existsById(taskId) && userServicePort.activeUserExistsById(userId)) {
            taskFollowerService.addFollower(taskId, userId);
            return RestApiResponse.success("Follower added successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeFollower(
            Long taskId,
            Long userId){
        if(taskRepo.existsById(taskId) && userServicePort.activeUserExistsById(userId)) {
            taskFollowerService.removeFollower(taskId, userId);
            return RestApiResponse.success("Follower removed successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse addFollowerByUserIds(
            Long taskId,
            List<Long> userIds){
        Set<Long> uniqueUserIds = userServicePort.getAllActiveUserIds(userIds);
        if(taskRepo.existsById(taskId) && !uniqueUserIds.isEmpty()) {
            taskFollowerService.addFollowerByUserIds(taskId, uniqueUserIds);
            return RestApiResponse.success("Followers added successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeFollowerByUserIds(
            Long taskId,
            List<Long> userIds){
        Set<Long> uniqueUserIds = userServicePort.getAllActiveUserIds(userIds);
        if(taskRepo.existsById(taskId) && !uniqueUserIds.isEmpty()) {
            taskFollowerService.removeFollowerByUserIds(taskId, uniqueUserIds);
            return RestApiResponse.success("Followers removed successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse getFollowers(Long taskId){
        List<Long> taskFollowerIds = taskFollowerService.getAllFollowerIdByTaskId(taskId);
        return userServicePort.getAllUserByIds(taskFollowerIds);
    }

    public TaskTimeLog addTimeLog(TimeLogDto timeLogDto){
        return taskTimeLogService.addTimeLog(timeLogDto);
    }

    public List<TaskTimeLog> getTimeLogsByTaskId(Long taskId){
        return taskTimeLogService.getTimeLogsByTaskId(taskId);
    }

    public List<TaskTimeLog> getTimeLogsByTaskIdAndUserId(Long taskId, Long userId){
        return taskTimeLogService.getTimeLogsByTaskIdAndUserId(taskId, userId);
    }

    public RestApiResponse deleteTimeLog(Long timeLogId){
        taskTimeLogService.deleteTimeLogById(timeLogId);
        return RestApiResponse.success("TimeLog deleted successfully");
    }
}
