package com.people.hub.taskmanager.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.taskmanager.dto.ProjectDto;
import com.people.hub.taskmanager.enums.StatusEnum;
import com.people.hub.taskmanager.model.*;
import com.people.hub.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody ProjectDto projectDto) {
        Project project = projectService.createProject(
                projectDto.getName(),
                projectDto.getDescription(),
                projectDto.getProjectCode(),
                projectDto.getStartDate(),
                projectDto.getEndDate(),
                projectDto.getStatus(),
                projectDto.getClientId(),
                projectDto.getManagerId());
        return ResponseEntity.status(201).body(project);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(projectService.getProjects(page, size, sortField, sortOrder));
    }

    @GetMapping("/members/{memberId}")
    public RestApiResponse getProjectsByMemberId(Long memberId, int page, int size, String sortField, String sortOrder){
        RestApiResponse response = projectService.getProjectsByMemberId(memberId, page, size, sortField, sortOrder);
        return RestApiResponse.success(response);
    }

    @GetMapping("/teams/{teamId}")
    public RestApiResponse getProjectsByTeamId(Long teamId, int page, int size, String sortField, String sortOrder){
        RestApiResponse response = projectService.getProjectsByTeamId(teamId, page, size, sortField, sortOrder);
        return RestApiResponse.success(response);
    }

    @PostMapping("/{projectId}/update")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long projectId,
            @RequestBody ProjectDto projectDto) {
        Project project = projectService.updateProject(
                projectId,
                projectDto.getName(),
                projectDto.getDescription(),
                projectDto.getProjectCode(),
                projectDto.getStartDate(),
                projectDto.getEndDate(),
                projectDto.getStatus(),
                projectDto.getClientId(),
                projectDto.getManagerId()
        );
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/delete")
    public ResponseEntity<RestApiResponse> deleteProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.deleteProject(projectId));
    }

    @PostMapping("/{projectId}/status/{status}")
    public ResponseEntity<Project> changeStatus(
            @PathVariable Long projectId,
            @PathVariable StatusEnum status) {
        return ResponseEntity.ok(projectService.changeStatus(projectId, status));
    }

    // MANAGER
    @PostMapping("/{projectId}/manager/{managerId}")
    public ResponseEntity<Project> assignManager(
            @PathVariable Long projectId,
            @PathVariable Long managerId) {

        return ResponseEntity.ok(projectService.assignManager(projectId, managerId));
    }

    // MEMBERS
    @PostMapping("/{projectId}/members/add/{memberId}")
    public ResponseEntity<RestApiResponse> addMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(projectService.addMember(projectId, memberId));
    }

    @PostMapping("/{projectId}/members/add")
    public ResponseEntity<RestApiResponse> addMemberByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(projectService.addMemberByIds(projectId, memberIds));
    }

    @PostMapping("/{projectId}/members/remove/{memberId}")
    public ResponseEntity<RestApiResponse> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(projectService.removeMember(projectId, memberId));
    }

    @PostMapping("/{projectId}/members/remove")
    public ResponseEntity<RestApiResponse> removeMemberByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> memberIds) {
        return ResponseEntity.ok(projectService.removeMemberByIds(projectId, memberIds));
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<RestApiResponse> getMembers(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        RestApiResponse response = projectService.getMembers(projectId, page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    // FOLLOWERS
    @PostMapping("/{projectId}/followers/add/{userId}")
    public ResponseEntity<RestApiResponse> addFollower(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.addFollower(projectId, userId));
    }

    @PostMapping("/{projectId}/followers/remove/{userId}")
    public ResponseEntity<RestApiResponse> removeFollower(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.removeFollower(projectId, userId));
    }

    @PostMapping("/{projectId}/followers/add")
    public ResponseEntity<RestApiResponse> addFollowerByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> userIds) {
        return ResponseEntity.ok(projectService.addFollowerByIds(projectId, userIds));
    }

    @PostMapping("/{projectId}/followers/remove")
    public ResponseEntity<RestApiResponse> removeFollowerByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> userIds) {
        return ResponseEntity.ok(projectService.removeFollowerByIds(projectId, userIds));
    }

    @GetMapping("/{projectId}/followers")
    public ResponseEntity<RestApiResponse> getFollowers(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        RestApiResponse response = projectService.getFollowers(projectId, page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    // TEAMS
    @PostMapping("/{projectId}/teams/add/{teamId}")
    public ResponseEntity<RestApiResponse> addTeam(
            @PathVariable Long projectId,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(projectService.addTeam(projectId, teamId));
    }

    @PostMapping("/{projectId}/teams/remove/{teamId}")
    public ResponseEntity<RestApiResponse> removeTeam(
            @PathVariable Long projectId,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(projectService.removeTeam(projectId, teamId));
    }

    @PostMapping("/{projectId}/teams/add")
    public ResponseEntity<RestApiResponse> addTeamByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> teamIds) {
        return ResponseEntity.ok(projectService.addTeamByIds(projectId, teamIds));
    }

    @PostMapping("/{projectId}/teams/remove")
    public ResponseEntity<RestApiResponse> removeTeamByIds(
            @PathVariable Long projectId,
            @RequestBody List<Long> teamIds) {
        return ResponseEntity.ok(projectService.removeTeamByIds(projectId, teamIds));
    }

    @GetMapping("/{projectId}/teams")
    public ResponseEntity<RestApiResponse> getTeams(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        RestApiResponse response = projectService.getTeams(projectId, page, size, sortField, sortOrder);
        return ResponseEntity.ok(response);
    }

    // TASKS
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<RestApiResponse> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(projectService.getProjectTasks(projectId, page, size, sortField, sortOrder));
    }
}
