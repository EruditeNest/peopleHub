package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.taskmanager.model.TaskMember;
import com.people.hub.taskmanager.repo.TaskMemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskMemberService {
    private final TaskMemberRepo taskMemberRepo;

    public void assignTaskByMemberId(Long taskId, Long memberId) {
        TaskMember taskMember = new TaskMember();
        taskMember.setMemberId(memberId);
        taskMember.setTaskId(taskId);
        taskMemberRepo.save(taskMember);
    }

    public void assignTaskByMemberIds(Long taskId, Set<Long> memberIds) {
        List<TaskMember> taskMemberList = new ArrayList<>();
        for(Long memberId: memberIds) {
            TaskMember taskMember = new TaskMember();
            taskMember.setMemberId(memberId);
            taskMember.setTaskId(taskId);
            taskMemberList.add(taskMember);
        }
        taskMemberRepo.saveAll(taskMemberList);
    }

    public int removeAssigneeByMemberId(Long taskId, Long memberId){
        return taskMemberRepo.deleteAllByTaskIdAndMemberId(taskId, memberId);
    }

    public int removeAssigneeByMemberIds(Long taskId, Set<Long> memberIds){
        return taskMemberRepo.deleteAllByTaskIdAndMemberIdIn(taskId, memberIds);
    }
}
