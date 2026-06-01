package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTeamRepo extends JpaRepository<ProjectTeam, Long> {
    Optional<ProjectTeam> findByProjectIdAndTeamId(Long projectId, Long teamId);

    boolean existsByProjectIdAndTeamId(
            Long projectId,
            Long teamId
    );

    @Query("""
       SELECT pt.teamId
       FROM ProjectTeam pt
       WHERE pt.projectId = :projectId
       AND pt.teamId IN :teamIds
       """)
    List<Long> findAllTeamIdsByProjectIdAndTeamIdsIN(
            @Param("projectId") Long projectId,
            @Param("teamIds") Collection<Long> teamIds
    );

    @Query("""
       SELECT pt.id
       FROM ProjectTeam pt
       WHERE pt.projectId = :projectId
       AND pt.teamId IN :teamIds
       """)
    List<Long> findExistingIdsByProjectIdAndMemberIdsIn(
            @Param("projectId") Long projectId,
            @Param("teamIds") Collection<Long> teamIds
    );

    @Query("""
       SELECT pt.teamId
       FROM ProjectTeam pt
       WHERE pt.projectId = :projectId
       """)
    List<Long> findTeamIdsByProjectId(@Param("projectId") Long projectId);

    @Query("""
       SELECT pt.projectId
       FROM ProjectTeam pt
       WHERE pt.teamId = :teamId
       """)
    List<Long> findProjectIdsByTeamId(@Param("teamId") Long teamId);

    @Transactional
    @Modifying
    void deleteByProjectIdAndTeamIdIn(Long projectId, Collection<Long> uniqueTeamIds);

    List<ProjectTeam> findAllByProjectId(Long projectId);

    void deleteByProjectIdAndTeamId(
            Long projectId,
            Long teamId
    );
}
