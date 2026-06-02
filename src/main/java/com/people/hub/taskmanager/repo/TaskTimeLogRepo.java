package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.TaskTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTimeLogRepo extends JpaRepository<TaskTimeLog, Long> {
    List<TaskTimeLog> findAllByTaskId(Long taskId);

    List<TaskTimeLog> findAllByTaskIdAndUserId(Long taskId, Long userId);
}
