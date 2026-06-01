package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.model.ProjectFollower;
import com.people.hub.taskmanager.repo.ProjectFollowerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectFollowerService {
    private final ProjectFollowerRepo projectFollowerRepo;

    public void addFollower(
            Long projectId,
            Long userId){
        ProjectFollower projectFollower = new ProjectFollower();
        projectFollower.setProjectId(projectId);
        projectFollower.setFollowerId(userId);
        projectFollowerRepo.save(projectFollower);
    }

    public void removeFollower(
            Long projectId,
            Long userId){
        if(projectFollowerRepo.existsByProjectIdAndFollowerId(projectId, userId)){
            projectFollowerRepo.deleteByProjectIdAndFollowerId(projectId, userId);
        } else {
            throw new NotFoundException("Follower(" + userId + ") does not exist in the project(" + projectId + ")");
        }
    }

    public void addFollowerByIds(
            Long projectId,
            Set<Long> userIds){
        List<ProjectFollower> projectFollowerList = new ArrayList<>();
        for(Long id: userIds) {
            ProjectFollower projectFollower = new ProjectFollower();
            projectFollower.setProjectId(projectId);
            projectFollower.setFollowerId(id);
            projectFollowerList.add(projectFollower);
        }
        projectFollowerRepo.saveAll(projectFollowerList);
    }

    public void removeFollowerByIds(
            Long projectId,
            Set<Long> userIds){
        Set<Long> uniqueFollowerIds = new HashSet<>(userIds);

        List<Long> existingFollowerIds = projectFollowerRepo.findAllFollowerIdsByProjectIdAndFollowerIdsIN(projectId, uniqueFollowerIds);
        Set<Long> existingSet = new HashSet<>(existingFollowerIds);

        List<Long> missingFollowerIds = uniqueFollowerIds.stream()
                .filter(followerId -> !existingSet.contains(followerId))
                .toList();
        if (!missingFollowerIds.isEmpty()) {
            throw new NotFoundException("Followers not found for projectId " + projectId + ": " + missingFollowerIds);
        }
        projectFollowerRepo.deleteByProjectIdAndFollowerIdIn(projectId, uniqueFollowerIds);
    }

    public List<Long> getFollowerIdsByProjectId(Long projectId) {
        return projectFollowerRepo.findFollowerIdsByProjectId(projectId);
    }
}
