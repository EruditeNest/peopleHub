package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.TaskChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskChecklistRepo extends JpaRepository<TaskChecklist, Long> {
    List<TaskChecklist> findAllByTaskId(Long taskId);
}
