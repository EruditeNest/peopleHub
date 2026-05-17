package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.taskmanager.model.Project;
import com.people.hub.taskmanager.model.Team;
import com.people.hub.taskmanager.model.TeamMember;
import com.people.hub.taskmanager.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepo teamRepo;

    public Team createTeam(CreateTeamRequest request){

    }

    public Team getTeamById(Long teamId){

    }

    public List<Team> getAllTeamsByIds(List<Long> teamIds){
        return teamRepo.findAllById(teamIds);
    }

    public RestApiResponse getTeams(Pageable pageable){

    }

    public Team updateTeam(
            Long teamId,
            UpdateTeamRequest request){

    }

    public void deleteTeam(Long teamId){

    }

    public Team assignManager(
            Long teamId,
            Long managerId){

    }

    public void addMember(
            Long teamId,
            Long memberId){

    }

    public void removeMember(
            Long teamId,
            Long memberId){

    }

    public List<TeamMember> getMembers(Long teamId){

    }

    public List<Project> getProjects(Long teamId){

    }
}
