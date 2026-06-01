package com.people.hub.taskmanager.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.taskmanager.model.Team;
import com.people.hub.taskmanager.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(@RequestParam String name, @RequestParam Long managerId) {
        return ResponseEntity.status(201).body(teamService.createTeam(name, managerId));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long teamId) {
        return ResponseEntity.ok(
                teamService.getTeamById(teamId));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getTeams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(teamService.getTeams(page, size, sortField, sortOrder));
    }

    @PostMapping("/{teamId}/update")
    public ResponseEntity<Team> updateTeam(
            @PathVariable Long teamId,
            @RequestParam String name,
            @RequestParam Long managerId) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, name, managerId));
    }

    @PostMapping("/{teamId}/delete")
    public ResponseEntity<RestApiResponse> deleteTeam(
            @PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.deleteTeam(teamId));
    }

    // MANAGER
    @PostMapping("/{teamId}/manager/{managerId}")
    public ResponseEntity<Team> assignManager(
            @PathVariable Long teamId,
            @PathVariable Long managerId) {
        return ResponseEntity.ok(
                teamService.assignManager(teamId, managerId));
    }

    // MEMBERS
    @PostMapping("/{teamId}/members/add/{memberId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {
        teamService.addMember(teamId, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/members/remove/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {

        teamService.removeMember(teamId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/members/add")
    public ResponseEntity<Void> addMembersById(
            @PathVariable Long teamId,
            @RequestBody List<Long> memberIds) {

        teamService.addMembersById(teamId, memberIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{teamId}/members/remove")
    public ResponseEntity<Void> removeMembersById(
            @PathVariable Long teamId,
            @RequestBody List<Long> memberIds) {

        teamService.removeMembersById(teamId, memberIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<RestApiResponse> getMembers(
            @PathVariable Long teamId) {
        RestApiResponse response = teamService.getMembers(teamId);
        return ResponseEntity.ok(response);
    }

    // PROJECTS
    @GetMapping("/{teamId}/projects")
    public ResponseEntity<RestApiResponse> getProjects(
            @PathVariable Long teamId) {
        RestApiResponse response = teamService.getProjects(teamId);
        return ResponseEntity.ok(response);
    }
}
