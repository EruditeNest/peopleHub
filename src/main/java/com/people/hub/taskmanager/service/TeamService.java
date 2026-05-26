package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.taskmanager.model.Project;
import com.people.hub.taskmanager.model.Team;
import com.people.hub.taskmanager.model.TeamMember;
import com.people.hub.taskmanager.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "managerId",
            "created_at",
            "updated_at"
    );
    private final TeamRepo teamRepo;

    public Team createTeam(CreateTeamRequest request){

    }

    public Team getTeamById(Long teamId){

    }

    public RestApiResponse getAllTeamsByIds(List<Long> teamIds, int page, int size, String sortField, String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Team> teams = teamRepo.findByIdIn(teamIds, pageable);
        PageInfo pageInfo = new PageInfo(teams.getNumber(), teams.getSize(), teams.getTotalElements());
        return RestApiResponse.success(pageInfo, teams.getContent());
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
