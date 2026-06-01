package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.service.UserServicePort;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.*;
import com.people.hub.taskmanager.repo.ProjectRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final ProjectMemberService projectMemberService;
    private final ProjectFollowerService projectFollowerService;
    private final ProjectTeamService projectTeamService;
    private final UserServicePort userServicePort;
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

        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Project> projects = projectRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(projects.getNumber(), projects.getSize(), projects.getTotalElements());
        return RestApiResponse.success(pageInfo, projects.getContent());
    }

    public RestApiResponse getProjectsByMemberId(Long memberId, int page, int size, String sortField, String sortOrder){
        log.info("getProjectsByMemberId for memberId: {}, with page: {}, size: {}, sortField: {}, sortOrder: {}", memberId, page, size, sortField, sortOrder);
        List<Long> projectIds = projectMemberService.getProjectsIdsByMemberId(memberId);

        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Project> projects = projectRepo.findAllByIdIn(projectIds, pageable);
        PageInfo pageInfo = new PageInfo(projects.getNumber(), projects.getSize(), projects.getTotalElements());
        return RestApiResponse.success(pageInfo, projects.getContent());
    }

    public RestApiResponse getProjectsByTeamId(Long teamId, int page, int size, String sortField, String sortOrder){
        List<Long> projectIds = projectTeamService.getProjectIdsByTeamId(teamId);

        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Project> projects = projectRepo.findAllByIdIn(projectIds, pageable);
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
        if (!isDeleted(projectId) && activeUserExistsById(memberId)) {
            projectMemberService.addMember(projectId, memberId);
            return RestApiResponse.success("Member(" + memberId + ") added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeMember(
            Long projectId,
            Long memberId){
        if (!isDeleted(projectId) && activeUserExistsById(memberId)) {
            projectMemberService.removeMember(projectId, memberId);
            return RestApiResponse.success("Member(" + memberId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse addMemberByIds(
            Long projectId,
            List<Long> memberIds){
        Set<Long> uniqueMemberIds = getAllActiveUserIds(memberIds);
        if(!isDeleted(projectId) && !uniqueMemberIds.isEmpty()) {
            projectMemberService.addMemberByIds(projectId, uniqueMemberIds);
            return RestApiResponse.success("Members(" + uniqueMemberIds + ") added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeMemberByIds(
            Long projectId,
            List<Long> memberIds){
        Set<Long> uniqueMemberIds = getAllActiveUserIds(memberIds);
        if(!isDeleted(projectId) && !uniqueMemberIds.isEmpty()) {
            projectMemberService.removeMemberByIds(projectId, uniqueMemberIds);
            return RestApiResponse.success("Member(" + uniqueMemberIds + ") removed from the project(" + projectId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse getMembers(Long projectId, int page, int size, String sortField, String sortOrder){
        List<Long> memberIds = projectMemberService.getMemberIdsByProjectId(projectId);
        return userServicePort.getAllUserByIds(memberIds, page, size, sortField, sortOrder);
    }

    public RestApiResponse addFollower(
            Long projectId,
            Long userId){
        if (!isDeleted(projectId) && activeUserExistsById(userId)) {
            projectFollowerService.addFollower(projectId, userId);
            return RestApiResponse.success("Follower(" + userId + ") added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeFollower(
            Long projectId,
            Long userId){
        if (!isDeleted(projectId) && activeUserExistsById(userId)) {
            projectFollowerService.removeFollower(projectId, userId);
            return RestApiResponse.success("Follower(" + userId + ") does not exist in the project(" + projectId + ")");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse addFollowerByIds(
            Long projectId,
            List<Long> userIds){
        Set<Long> uniqueFollowerIds = getAllActiveUserIds(userIds);
        if(!isDeleted(projectId) && !uniqueFollowerIds.isEmpty()) {
            projectFollowerService.addFollowerByIds(projectId, uniqueFollowerIds);
            return RestApiResponse.success("Follower(" + uniqueFollowerIds + ") added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeFollowerByIds(
            Long projectId,
            List<Long> userIds){
        Set<Long> uniqueFollowerIds = getAllActiveUserIds(userIds);
        if(!isDeleted(projectId) && !uniqueFollowerIds.isEmpty()) {
            projectFollowerService.removeFollowerByIds(projectId, uniqueFollowerIds);
            return RestApiResponse.success("Follower(" + uniqueFollowerIds + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse getFollowers(Long projectId, int page, int size, String sortField, String sortOrder){
        List<Long> followerIds = projectFollowerService.getFollowerIdsByProjectId(projectId);
        return userServicePort.getAllUserByIds(followerIds, page, size, sortField, sortOrder);
    }

    public RestApiResponse addTeam(
            Long projectId,
            Long teamId){
        if (!isDeleted(projectId) && teamExistsById(teamId)) {
            projectTeamService.addTeam(projectId, teamId);
            return RestApiResponse.success("Team(" + teamId + ") added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeTeam(
            Long projectId,
            Long teamId){
        if (!isDeleted(projectId) && teamExistsById(teamId)) {
            projectTeamService.removeTeam(projectId, teamId);
            return RestApiResponse.success("Team(" + teamId + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse addTeamByIds(
            Long projectId,
            List<Long> teamIds){
        Set<Long> uniqueTeamIds = getAllExistingTeamIds(teamIds);
        if(!isDeleted(projectId) && !uniqueTeamIds.isEmpty()) {
            projectTeamService.addTeamByIds(projectId, uniqueTeamIds);
            return RestApiResponse.success("All Teams added to the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse removeTeamByIds(
            Long projectId,
            List<Long> teamIds){
        Set<Long> uniqueTeamIds = getAllExistingTeamIds(teamIds);
        if(!isDeleted(projectId) && !uniqueTeamIds.isEmpty()) {
            projectTeamService.removeTeamByIds(projectId, uniqueTeamIds);
            return RestApiResponse.success("Team(" + teamIds + ") removed from the project(" + projectId + ") successfully");
        }
        return RestApiResponse.failure();
    }

    public RestApiResponse getTeams(Long projectId, int page, int size, String sortField, String sortOrder){
        List<Long> teamIds = projectTeamService.getTeamIdsByProjectId(projectId);
        return teamService.getAllTeamsByIds(teamIds, page, size, sortField, sortOrder);
    }

    public RestApiResponse getProjectTasks(Long projectId, int page, int size, String sortField, String sortOrder){
        return taskService.getAllByProjectId(projectId, page, size, sortField, sortOrder);
    }

    private boolean isDeleted(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found", projectId));
        return project.isDeleted();
    }

    private boolean activeUserExistsById(Long id){
        return userServicePort.activeUserExistsById(id);
    }

    private Set<Long> getAllActiveUserIds(List<Long> ids){
        return userServicePort.getAllActiveUserIds(ids);
    }

    private boolean teamExistsById(Long id) {
        return teamService.existsById(id);
    }

    private Set<Long> getAllExistingTeamIds(List<Long> ids) {
        return teamService.getAllExistingTeamIds(ids);
    }
}
