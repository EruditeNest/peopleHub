package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectMember;
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
public interface ProjectMemberRepo extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectIdAndMemberId(Long projectId, Long memberId);

    boolean existsByProjectIdAndMemberId(
            Long projectId,
            Long memberId
    );

    @Query("""
       SELECT pm.memberId
       FROM ProjectMember pm
       WHERE pm.projectId = :projectId
       AND pm.memberId IN :memberIds
       """)
    List<Long> findAllMemberIdsByProjectIdAndMemberIdsIN(
            @Param("projectId") Long projectId,
            @Param("memberIds") Collection<Long> memberIds
    );

    @Query("""
       SELECT pm.id
       FROM ProjectMember pm
       WHERE pm.projectId = :projectId
       AND pm.memberId IN :memberIds
       """)
    List<Long> findExistingIdsByProjectIdAndMemberIdsIn(
            @Param("projectId") Long projectId,
            @Param("memberIds") Collection<Long> memberIds
    );

    @Query("""
       SELECT pm.memberId
       FROM ProjectMember pm
       WHERE pm.projectId = :projectId
       """)
    List<Long> findMemberIdsByProjectId(@Param("projectId") Long projectId);

    @Query("""
       SELECT pm.projectId
       FROM ProjectMember pm
       WHERE pm.memberId = :memberId
       """)
    List<Long> findProjectIdsByMemberId(@Param("memberId") Long memberId);

    @Transactional
    @Modifying
    int deleteByProjectIdAndMemberIdIn(Long projectId, Collection<Long> uniqueMemberIds);

    List<ProjectMember> findAllByProjectId(Long projectId);

    int deleteByProjectIdAndMemberId(
            Long projectId,
            Long memberId
    );
}
