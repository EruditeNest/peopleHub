package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectFollower;
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
public interface ProjectFollowerRepo extends JpaRepository<ProjectFollower, Long> {
    Optional<ProjectFollower> findByProjectIdAndFollowerId(Long projectId, Long followerId);

    boolean existsByProjectIdAndFollowerId(
            Long projectId,
            Long followerId
    );

    @Query("""
       SELECT pf.followerId
       FROM ProjectFollower pf
       WHERE pf.projectId = :projectId
       AND pf.followerId IN :followerIds
       """)
    List<Long> findAllFollowerIdsByProjectIdAndFollowerIdsIN(
            @Param("projectId") Long projectId,
            @Param("followerIds") Collection<Long> followerIds
    );

    @Query("""
       SELECT pf.id
       FROM ProjectFollower pf
       WHERE pf.projectId = :projectId
       AND pf.followerId IN :followerIds
       """)
    List<Long> findExistingIdsByProjectIdAndFollowerIdsIn(
            @Param("projectId") Long projectId,
            @Param("followerIds") Collection<Long> followerIds
    );

    @Query("""
       SELECT pf.followerId
       FROM ProjectFollower pf
       WHERE pf.projectId = :projectId
       """)
    List<Long> findFollowerIdsByProjectId(@Param("projectId") Long projectId);

    @Transactional
    @Modifying
    void deleteByProjectIdAndFollowerIdIn(Long projectId, Collection<Long> uniqueFollowerIds);

    List<ProjectFollower> findAllByProjectId(Long projectId);

    void deleteByProjectIdAndFollowerId(
            Long projectId,
            Long followerId
    );
}
