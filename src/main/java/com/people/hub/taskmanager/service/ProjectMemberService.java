package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.model.ProjectMember;
import com.people.hub.taskmanager.repo.ProjectMemberRepo;
import com.people.hub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectMemberRepo projectMemberRepo;

    public void addMember(
            Long projectId,
            Long memberId){
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        projectMember.setMemberId(memberId);
        projectMemberRepo.save(projectMember);
    }

    public void removeMember(
            Long projectId,
            Long memberId){
        if(projectMemberRepo.existsByProjectIdAndMemberId(projectId, memberId)){
            projectMemberRepo.deleteByProjectIdAndMemberId(projectId, memberId);
        } else {
            throw new NotFoundException("Member in project " + projectId +" not found", memberId);
        }
    }

    public void addMemberByIds(
            Long projectId,
            Set<Long> memberIds){
        List<ProjectMember> allNewMembers = new ArrayList<>();
        for(Long id: memberIds) {
            ProjectMember projectMember = new ProjectMember();
            projectMember.setProjectId(projectId);
            projectMember.setMemberId(id);
            allNewMembers.add(projectMember);
        }
        projectMemberRepo.saveAll(allNewMembers);
    }

    public void removeMemberByIds(
            Long projectId,
            Set<Long> memberIds){
        Set<Long> uniqueMemberIds = new HashSet<>(memberIds);

        List<Long> existingMemberIds = projectMemberRepo.findAllMemberIdsByProjectIdAndMemberIdsIN(projectId, uniqueMemberIds);
        Set<Long> existingSet = new HashSet<>(existingMemberIds);

        List<Long> missingMemberIds = uniqueMemberIds.stream()
                .filter(memberId -> !existingSet.contains(memberId))
                .toList();
        if (!missingMemberIds.isEmpty()) {
            throw new NotFoundException("Members not found for projectId " + projectId + ": " + missingMemberIds);
        }
        projectMemberRepo.deleteByProjectIdAndMemberIdIn(projectId, uniqueMemberIds);
    }

    public List<Long> getMemberIdsByProjectId(Long projectId){
        return projectMemberRepo.findMemberIdsByProjectId(projectId);
    }

    public List<Long> getProjectsIdsByMemberId(Long memberId){
        return projectMemberRepo.findProjectIdsByMemberId(memberId);
    }
}
