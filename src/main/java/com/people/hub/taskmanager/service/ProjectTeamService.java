package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.model.ProjectTeam;
import com.people.hub.taskmanager.repo.ProjectTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectTeamService {
    private final ProjectTeamRepo projectTeamRepo;

    public void addTeam(
            Long projectId,
            Long teamId){
        ProjectTeam projectTeam = new ProjectTeam();
        projectTeam.setTeamId(teamId);
        projectTeam.setProjectId(projectId);
        projectTeamRepo.save(projectTeam);
    }

    public void removeTeam(
            Long projectId,
            Long teamId){
        if(projectTeamRepo.existsByProjectIdAndTeamId(projectId, teamId)){
            projectTeamRepo.deleteByProjectIdAndTeamId(projectId, teamId);
        }
        throw new NotFoundException("Team(" + teamId + ") does not exist in the project(" + projectId + ")");
    }

    public void addTeamByIds(
            Long projectId,
            List<Long> teamIds){
        List<ProjectTeam> projectTeamsToSave = new ArrayList<>();
        for(Long teamId: teamIds) {
            ProjectTeam projectTeam = new ProjectTeam();
            projectTeam.setTeamId(teamId);
            projectTeam.setProjectId(projectId);
            projectTeamsToSave.add(projectTeam);
        }
        projectTeamRepo.saveAll(projectTeamsToSave);
    }

    public void removeTeamByIds(
            Long projectId,
            List<Long> teamIds){
        Set<Long> uniqueTeamIds = new HashSet<>(teamIds);

        List<Long> existingTeamIds = projectTeamRepo.findAllTeamIdsByProjectIdAndTeamIdsIN(projectId, uniqueTeamIds);
        Set<Long> existingSet = new HashSet<>(existingTeamIds);

        List<Long> missingTeamIds = uniqueTeamIds.stream()
                .filter(teamId -> !existingSet.contains(teamId))
                .toList();
        if (!missingTeamIds.isEmpty()) {
            throw new NotFoundException("Members not found for projectId " + projectId + ": " + missingTeamIds);
        }
        projectTeamRepo.deleteByProjectIdAndTeamIdIn(projectId, uniqueTeamIds);
    }

    public List<Long> getTeamIdsByProjectId(Long projectId){
        return projectTeamRepo.findTeamIdsByProjectId(projectId);
    }
}
