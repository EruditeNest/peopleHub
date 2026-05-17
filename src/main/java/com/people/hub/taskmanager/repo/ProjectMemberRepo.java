package com.people.hub.taskmanager.repo;

import com.people.hub.taskmanager.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepo extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectIdAndMemberId(Long projectId, Long memberId);

    boolean existsByProjectIdAndMemberId(
            Long projectId,
            Long memberId
    );

    List<ProjectMember> findAllByProjectId(Long projectId);

    void deleteByProjectIdAndMemberId(
            Long projectId,
            Long memberId
    );
}
