package com.people.hub.taskmanager.controller;

import com.people.hub.taskmanager.model.Team;
import com.people.hub.taskmanager.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Slf4j
public class TeamController {

    private final TeamService teamService;

    // CRUD
    @PostMapping
    public ResponseEntity<Team> createTeam(
            @RequestBody CreateTeamRequest request) {

        return ResponseEntity.ok(
                teamService.createTeam(request));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeamById(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getTeamById(teamId));
    }

    @GetMapping
    public ResponseEntity<Page<Team>> getTeams(
            Pageable pageable) {

        return ResponseEntity.ok(
                teamService.getTeams(pageable));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Team> updateTeam(
            @PathVariable Long teamId,
            @RequestBody UpdateTeamRequest request) {

        return ResponseEntity.ok(
                teamService.updateTeam(teamId, request));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long teamId) {

        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    // MANAGER
    @PatchMapping("/{teamId}/manager/{managerId}")
    public ResponseEntity<Team> assignManager(
            @PathVariable Long teamId,
            @PathVariable Long managerId) {

        return ResponseEntity.ok(
                teamService.assignManager(teamId, managerId));
    }

    // MEMBERS
    @PostMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {

        teamService.addMember(teamId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {

        teamService.removeMember(teamId, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMember>> getMembers(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getMembers(teamId));
    }

    // PROJECTS
    @GetMapping("/{teamId}/projects")
    public ResponseEntity<List<Project>> getProjects(
            @PathVariable Long teamId) {

        return ResponseEntity.ok(
                teamService.getProjects(teamId));
    }
}
