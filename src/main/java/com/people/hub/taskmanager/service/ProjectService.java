package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.*;
import com.people.hub.taskmanager.repo.ProjectFollowerRepo;
import com.people.hub.taskmanager.repo.ProjectMemberRepo;
import com.people.hub.taskmanager.repo.ProjectRepo;
import com.people.hub.taskmanager.repo.ProjectTeamRepo;
import com.people.hub.user.model.User;
import com.people.hub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "projectCode",
            "status",
            "managerId",
            "clientId",
            "startDate",
            "endDate",
            "isDeleted",
            "created_at",
            "updated_at"
    );

    private final ProjectRepo projectRepo;
    private final ProjectMemberRepo projectMemberRepo;
    private final ProjectFollowerRepo projectFollowerRepo;
    private final ProjectTeamRepo projectTeamRepo;
    private final UserService userService;
    private final TeamService teamService;
    private final TaskService taskService;

    public Project createProject(
            String name,
            String description,
            String projectCode,
            LocalDate startDate,
            LocalDate endDate,
            StatusEnum status,
            Long clientId,
            Long managerId
    ){
        Project project = new Project();
        project.setProjectCode(projectCode);
        project.setDescription(description);
        project.setName(name);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setStatus(status);
        project.setClientId(clientId);
        project.setManagerId(managerId);
        project.setDeleted(false);
        return projectRepo.save(project);
    }

    public Project getProjectById(Long projectId){
        return projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
    }

    public RestApiResponse getProjects(int page, int size, String sortField, String sortOrder){
        log.info("getProjects with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "created_at";
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Project> projects = projectRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(projects.getNumber(), projects.getSize(), projects.getTotalElements());
        return RestApiResponse.success(pageInfo, projects.getContent());
    }

    public Project updateProject(
            Long projectId,
            String name,
            String description,
            String projectCode,
            LocalDate startDate,
            LocalDate endDate,
            StatusEnum status,
            Long clientId,
            Long managerId
    ){
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
        project.setProjectCode(projectCode);
        project.setDescription(description);
        project.setManagerId(managerId);
        project.setClientId(clientId);
        project.setName(name);
        project.setStatus(status);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        return projectRepo.save(project);
    }

    /*
    * TODO: The project that is deleted should be removed and nobody should be able to access them.
    */
    public RestApiResponse deleteProject(Long projectId){
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
        project.setDeleted(true);
        projectRepo.save(project);
        return RestApiResponse.success("Project deleted successfully");
    }

    public Project changeStatus(
            Long projectId,
            StatusEnum status){
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
        project.setStatus(status);
        return projectRepo.save(project);
    }

    public Project assignManager(
            Long projectId,
            Long managerId){
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
        project.setManagerId(managerId);
        return projectRepo.save(project);
    }

    public RestApiResponse addMember(
            Long projectId,
            Long memberId){
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProjectId(projectId);
        projectMember.setMemberId(memberId);
        projectMemberRepo.save(projectMember);
        return RestApiResponse.success("Member(" + memberId + ") added to the project(" + projectId + ") successfully");
    }

    public RestApiResponse removeMember(
            Long projectId,
            Long memberId){
        if(projectMemberRepo.existsByProjectIdAndMemberId(projectId, memberId)){
            projectMemberRepo.deleteByProjectIdAndMemberId(projectId, memberId);
            return RestApiResponse.success("Member(" + memberId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.success("Member(" + memberId + ") does not exist in the project(" + projectId + ")");
    }

    public List<User> getMembers(Long projectId){
        List<ProjectMember> projectMembers = projectMemberRepo.findAllByProjectId(projectId);
        List<Long> memberIds = projectMembers.stream()
                .map(ProjectMember::getMemberId)
                .toList();
        List<User> users = userService.getAllUserByIds(memberIds);
        return users;
    }

    public RestApiResponse addFollower(
            Long projectId,
            Long userId){
        ProjectFollower projectFollower = new ProjectFollower();
        projectFollower.setProjectId(projectId);
        projectFollower.setFollowerId(userId);
        projectFollowerRepo.save(projectFollower);
        return RestApiResponse.success("Follower(" + userId + ") added to the project(" + projectId + ") successfully");
    }

    public RestApiResponse removeFollower(
            Long projectId,
            Long userId){
        if(projectFollowerRepo.existsByProjectIdAndFollowerId(projectId, userId)){
            projectFollowerRepo.deleteByProjectIdAndFollowerId(projectId, userId);
            return RestApiResponse.success("Follower(" + userId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.success("Follower(" + userId + ") does not exist in the project(" + projectId + ")");
    }

    public List<User> getFollowers(Long projectId){
        List<ProjectFollower> projectFollowers = projectFollowerRepo.findAllByProjectId(projectId);
        List<Long> followerIds = projectFollowers.stream()
                .map(ProjectFollower::getFollowerId)
                .toList();
        List<User> users = userService.getAllUserByIds(followerIds);
        return users;
    }

    public RestApiResponse addTeam(
            Long projectId,
            Long teamId){
        ProjectTeam projectTeam = new ProjectTeam();
        projectTeam.setTeamId(teamId);
        projectTeam.setProjectId(projectId);
        projectTeamRepo.save(projectTeam);
        return RestApiResponse.success("Team(" + teamId + ") added to the project(" + projectId + ") successfully");
    }

    public RestApiResponse removeTeam(
            Long projectId,
            Long teamId){
        if(projectTeamRepo.existsByProjectIdAndTeamId(projectId, teamId)){
            projectTeamRepo.deleteByProjectIdAndTeamId(projectId, teamId);
            return RestApiResponse.success("Team(" + teamId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.success("Team(" + teamId + ") does not exist in the project(" + projectId + ")");
    }

    public RestApiResponse addTeamByIds(
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
        return RestApiResponse.success("All Teams added to the project(" + projectId + ") successfully");
    }

    public RestApiResponse removeTeamByIds(
            Long projectId,
            List<Long> teamIds){
        if(projectTeamRepo.existsByProjectIdAndTeamId(projectId, teamId)){
            projectTeamRepo.deleteByProjectIdAndTeamId(projectId, teamId);
            return RestApiResponse.success("Team(" + teamId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.success("Team(" + teamId + ") does not exist in the project(" + projectId + ")");
    }

    public List<Team> getTeams(Long projectId){
        List<ProjectTeam> projectTeams = projectTeamRepo.findAllByProjectId(projectId);
        List<Long> teamIds = projectTeams.stream()
                .map(ProjectTeam::getTeamId)
                .toList();
        List<Team> teams = teamService.getAllTeamsByIds(teamIds);
        return teams;
    }

    public RestApiResponse getProjectTasks(Long projectId, int page, int size, String sortField, String sortOrder){
        return taskService.getAllByProjectId(projectId, page, size, sortField, sortOrder);
    }
}
