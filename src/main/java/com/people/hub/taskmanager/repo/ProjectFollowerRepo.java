package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectFollowerRepo extends JpaRepository<ProjectFollower, Long> {
    Optional<ProjectFollower> findByProjectIdAndFollowerId(Long projectId, Long followerId);

    boolean existsByProjectIdAndFollowerId(
            Long projectId,
            Long followerId
    );

    List<ProjectFollower> findAllByProjectId(Long projectId);

    void deleteByProjectIdAndFollowerId(
            Long projectId,
            Long followerId
    );
}
