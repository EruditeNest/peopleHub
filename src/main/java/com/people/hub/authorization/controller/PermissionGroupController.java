package com.people.hub.authorization.controller;

import com.people.hub.authorization.model.PermissionGroup;
import com.people.hub.authorization.service.PermissionGroupService;
import com.people.hub.common.RestApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/permission-group")
@RequiredArgsConstructor
@Slf4j
public class PermissionGroupController {

    private final PermissionGroupService permissionGroupService;

    @PostMapping("create")
    public ResponseEntity<PermissionGroup> createPermissionGroup(
            @RequestParam String name,
            @RequestParam String description) {
        log.info("POST Creating permission group with name: {}, description: {}", name, description);
        PermissionGroup created = permissionGroupService.createPermissionGroup(name, description);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/update/{id}")
    public ResponseEntity<PermissionGroup> updatePermissionGroup(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description) {
        log.info("GET /permission-group/{} - Updating permission group", id);
        PermissionGroup updated = permissionGroupService.updatePermissionGroup(id, name, description);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<Set<PermissionGroup>> getAllPermissionGroups() {
        log.info("GET /permission-group - Fetching all permission groups");
        Set<PermissionGroup> groups = permissionGroupService.getAllPermissionGroup();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<RestApiResponse> deletePermissionGroup(@PathVariable Long id) {
        log.info("GET /permission-group/{} - Deleting permission group", id);
        permissionGroupService.deletePermissionGroup(id);
        return ResponseEntity.ok(RestApiResponse.success());
    }

    @DeleteMapping("/delete-by-ids")
    public ResponseEntity<RestApiResponse> deletePermissionGroupsByIds(@RequestBody Set<Long> ids) {
        log.info("GET /permission-group - Deleting permission groups with ids: {}", ids);
        permissionGroupService.deletePermissionGroupsById(ids);
        return ResponseEntity.ok(RestApiResponse.success());
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<PermissionGroup>> getPermissionGroupsByIds(@RequestBody Set<Long> ids) {
        log.info("POST /api/permission-groups/batch - Fetching permission groups with ids: {}", ids);
        List<PermissionGroup> groups = permissionGroupService.getAllPermissionGroupsById(ids);
        return ResponseEntity.ok(groups);
    }
}
