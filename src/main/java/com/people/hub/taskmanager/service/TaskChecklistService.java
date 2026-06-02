package com.people.hub.taskmanager.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.dto.ChecklistDto;
import com.people.hub.taskmanager.model.TaskChecklist;
import com.people.hub.taskmanager.repo.TaskChecklistRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskChecklistService {
    private final TaskChecklistRepo taskChecklistRepo;

    public TaskChecklist addChecklist(ChecklistDto checklistDto, Long userId) {
        TaskChecklist checklist = new TaskChecklist();
        checklist.setName(checklistDto.getName());
        checklist.setDescription(checklist.getDescription());
        checklist.setTaskId(checklistDto.getTaskId());
        checklist.setCompleted(false);
        checklist.setCreatedBy(userId);
        return taskChecklistRepo.save(checklist);
    }

    public TaskChecklist updateChecklist(Long checklistId, ChecklistDto checklistDto, Long userId) {
        TaskChecklist checklist = taskChecklistRepo.findById(checklistId)
                .orElseThrow(() -> new NotFoundException("Checklist not found"));
        checklist.setName(checklistDto.getName());
        checklist.setUpdatedBy(userId);
        checklist.setDescription(checklistDto.getDescription());
        return taskChecklistRepo.save(checklist);
    }

    public void markChecklistComplete(Long checklistId, Long userId) {
        TaskChecklist checklist = taskChecklistRepo.findById(checklistId)
                .orElseThrow(() -> new NotFoundException("Checklist not found"));
        checklist.setCompleted(true);
        checklist.setCompletedBy(userId);
        taskChecklistRepo.save(checklist);
    }

    public void markChecklistUncomplete(Long checklistId) {
        TaskChecklist checklist = taskChecklistRepo.findById(checklistId)
                .orElseThrow(() -> new NotFoundException("Checklist not found"));
        checklist.setCompleted(false);
        checklist.setCompletedBy(null);
        taskChecklistRepo.save(checklist);
    }

    public void deleteChecklist(Long checklistId) {
        taskChecklistRepo.deleteById(checklistId);
    }

    public List<TaskChecklist> getChecklists(Long taskId) {
        return taskChecklistRepo.findAllByTaskId(taskId);
    }
}
