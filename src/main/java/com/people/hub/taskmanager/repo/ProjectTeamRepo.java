package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTeamRepo extends JpaRepository<ProjectTeam, Long> {
    Optional<ProjectTeam> findByProjectIdAndTeamId(Long projectId, Long teamId);

    boolean existsByProjectIdAndTeamId(
            Long projectId,
            Long teamId
    );

    List<ProjectTeam> findAllByProjectId(Long projectId);

    void deleteByProjectIdAndTeamId(
            Long projectId,
            Long teamId
    );
}
