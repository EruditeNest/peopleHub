package com.people.hub.taskmanager.service;

import com.people.hub.taskmanager.model.TaskFollower;
import com.people.hub.taskmanager.repo.TaskFollowerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskFollowerService {
    private final TaskFollowerRepo taskFollowerRepo;

    public void addFollower(Long taskId, Long userId) {
        TaskFollower taskFollower = new TaskFollower();
        taskFollower.setTaskId(taskId);
        taskFollower.setUserId(userId);
        taskFollowerRepo.save(taskFollower);
    }

    public int removeFollower(Long taskId, Long userId) {
        return taskFollowerRepo.deleteByTaskIdAndUserId(taskId, userId);
    }

    public void addFollowerByUserIds(Long taskId, Set<Long> uniqueUserIds) {
        List<TaskFollower> taskFollowerList = new ArrayList<>();
        for(Long id: uniqueUserIds) {
            TaskFollower taskFollower = new TaskFollower();
            taskFollower.setUserId(id);
            taskFollower.setTaskId(taskId);
            taskFollowerList.add(taskFollower);
        }
        taskFollowerRepo.saveAll(taskFollowerList);
    }

    public int removeFollowerByUserIds(Long taskId, Set<Long> uniqueUserIds) {
        return taskFollowerRepo.deleteAllByTaskIdAndUserIdIn(taskId, uniqueUserIds);
    }

    public List<Long> getAllFollowerIdByTaskId(Long taskId) {
        return taskFollowerRepo.findAllUserIdByTaskId(taskId);
    }
}
