package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.service.UserServicePort;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.taskmanager.model.Team;
import com.people.hub.taskmanager.repo.TeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final TeamMemberService teamMemberService;
    private final TeamRepo teamRepo;
    private final UserServicePort userServicePort;
    private final ProjectTeamService projectTeamService;

    public Team createTeam(String name, Long managerId){
        Team team = new Team();
        team.setName(name);
        team.setManagerId(managerId);
        return teamRepo.save(team);
    }

    public Team getTeamById(Long teamId){
        return teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found", teamId));
    }

    public RestApiResponse getAllTeamsByIds(List<Long> teamIds, int page, int size, String sortField, String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Team> teams = teamRepo.findByIdIn(teamIds, pageable);
        PageInfo pageInfo = new PageInfo(teams.getNumber(), teams.getSize(), teams.getTotalElements());
        return RestApiResponse.success(pageInfo, teams.getContent());
    }

    public RestApiResponse getTeams(int page, int size, String sortField, String sortOrder){
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Team> teams = teamRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(teams.getNumber(), teams.getSize(), teams.getTotalElements());
        return RestApiResponse.success(pageInfo, teams.getContent());
    }

    public Team updateTeam(Long teamId, String name, Long managerId){
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found", teamId));
        team.setName(name);
        team.setManagerId(managerId);
        return teamRepo.save(team);
    }

    public RestApiResponse deleteTeam(Long teamId){
        int deletedRelations = teamMemberService.deleteAllByTeamId(teamId);
        teamRepo.deleteById(teamId);
        if(deletedRelations > 0) {
            return RestApiResponse.success("Team deleted successfully");
        }
        return RestApiResponse.success("Team deleted successfully - no member found");
    }

    public Team assignManager(
            Long teamId,
            Long managerId){
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new NotFoundException("Team not found", teamId));
        team.setManagerId(managerId);
        return teamRepo.save(team);
    }

    public RestApiResponse addMember(
            Long teamId,
            Long memberId){
        if(existsById(teamId) && userServicePort.activeUserExistsById(memberId)) {
            teamMemberService.addMember(teamId, memberId);
            return RestApiResponse.success("Member (" + memberId + ") added successfully in team (" + teamId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeMember(
            Long teamId,
            Long memberId){
        if(existsById(teamId) && userServicePort.activeUserExistsById(memberId)) {
            teamMemberService.removeMember(teamId, memberId);
            return RestApiResponse.success("Member (" + memberId + ") removed successfully from team (" + teamId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse addMemberByIds(
            Long teamId,
            List<Long> memberIds){
        Set<Long> uniqueMemberIds = userServicePort.getAllActiveUserIds(memberIds);
        if(existsById(teamId) && !uniqueMemberIds.isEmpty()) {
            teamMemberService.addMemberByIds(teamId, uniqueMemberIds);
            return RestApiResponse.success("Member (" + uniqueMemberIds + ") added successfully in team (" + teamId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeMemberByIds(
            Long teamId,
            List<Long> memberIds){
        Set<Long> uniqueMemberIds = userServicePort.getAllActiveUserIds(memberIds);
        if(existsById(teamId) && !uniqueMemberIds.isEmpty()) {
            teamMemberService.removeMemberByIds(teamId, uniqueMemberIds);
            return RestApiResponse.success("Member (" + uniqueMemberIds + ") removed successfully from team (" + teamId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse getMembers(Long teamId, int page, int size, String sortField, String sortOrder){
        List<Long> memberIds = teamMemberService.getMemberIdsByTeamId(teamId);
        return userServicePort.getAllUserByIds(memberIds, page, size, sortField, sortOrder);
    }

    public boolean existsById(Long id){
        return teamRepo.existsById(id);
    }

    public Set<Long> getAllExistingTeamIds(List<Long> ids) {
        return teamRepo.findAllById(ids).stream()
                .map(Team::getId)
                .collect(Collectors.toSet());
    }
}
