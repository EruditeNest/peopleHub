package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.*;
import com.people.hub.taskmanager.repo.TaskRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private static final Set<String> ALLOWED_TASK_SORT_FIELD = Set.of(

    );

    private final TaskRepo taskRepo;

    public Task createTask(CreateTaskRequest request){

    }

    public Task getTaskById(Long taskId){

    }

    public RestApiResponse getAllByProjectId(Long projectId, int page, int size, String sortField, String sortOrder){
        log.info("getProjectTasks with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        if (!ALLOWED_TASK_SORT_FIELD.contains(sortField)) {
            sortField = "created_at";
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> projects = taskRepo.findAllByProjectId(projectId, pageable);
        PageInfo pageInfo = new PageInfo(projects.getNumber(), projects.getSize(), projects.getTotalElements());
        return RestApiResponse.success(pageInfo, projects.getContent());
    }

    public RestApiResponse getTasks(Pageable pageable){

    }

    public Task updateTask(
            Long taskId,
            UpdateTaskRequest request){

    }

    public void deleteTask(Long taskId){

    }

    public Task changeStatus(
            Long taskId,
            StatusEnum status){

    }

    public Task assignTask(
            Long taskId,
            Long userId){

    }

    public TaskChecklist addChecklist(
            Long taskId,
            CreateChecklistRequest request){

    }

    public TaskChecklist updateChecklist(
            Long checklistId,
            UpdateChecklistRequest request){

    }

    public void markChecklistComplete(Long checklistId){

    }

    public void deleteChecklist(Long checklistId){

    }

    public List<TaskChecklist> getChecklists(Long taskId){

    }

    public void addFollower(
            Long taskId,
            Long userId){

    }

    public void removeFollower(
            Long taskId,
            Long userId){

    }

    public List<TaskFollower> getFollowers(Long taskId){

    }

    public TaskTimeLog addTimeLog(
            Long taskId,
            CreateTaskTimeLogRequest request){

    }

    public List<TaskTimeLog> getTimeLogs(Long taskId){

    }

    public void deleteTimeLog(Long timeLogId){

    }
}
