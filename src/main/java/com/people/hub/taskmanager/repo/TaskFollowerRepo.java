package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.TaskFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TaskFollowerRepo extends JpaRepository<TaskFollower, Long> {
    int deleteByTaskIdAndUserId(Long taskId, Long userId);

    int deleteAllByTaskIdAndUserIdIn(Long taskId, Set<Long> uniqueUserIds);

    List<TaskFollower> findAllByTaskId(Long taskId);

    @Query("""
        SELECT tf.userId
        from TaskFollower tf
        WHERE tf.taskId = :taskId
    """)
    List<Long> findAllUserIdByTaskId(Long taskId);
}
