package com.people.hub.taskmanager.service;

import com.people.hub.taskmanager.dto.TimeLogDto;
import com.people.hub.taskmanager.model.TaskTimeLog;
import com.people.hub.taskmanager.repo.TaskTimeLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskTimeLogService {
    private final TaskTimeLogRepo taskTimeLogRepo;

    public TaskTimeLog addTimeLog(TimeLogDto timeLogDto) {
        TaskTimeLog timeLog = new TaskTimeLog();
        timeLog.setTaskId(timeLogDto.getTaskId());
        timeLog.setDescription(timeLogDto.getDescription());
        timeLog.setUserId(timeLogDto.getUserId());
        timeLog.setWorkDate(timeLogDto.getWorkDate());
        timeLog.setStartTime(timeLogDto.getStartTime());
        timeLog.setEndTime(timeLogDto.getEndTime());
        return taskTimeLogRepo.save(timeLog);
    }

    public void deleteTimeLogById(Long timeLogId) {
        taskTimeLogRepo.deleteById(timeLogId);
    }

    public List<TaskTimeLog> getTimeLogsByTaskId(Long taskId) {
        return taskTimeLogRepo.findAllByTaskId(taskId);
    }

    public List<TaskTimeLog> getTimeLogsByTaskIdAndUserId(Long taskId, Long userId) {
        return taskTimeLogRepo.findAllByTaskIdAndUserId(taskId, userId);
    }
}
