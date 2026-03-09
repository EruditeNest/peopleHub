package com.people.hub.authorization.controller;

import com.people.hub.authorization.dto.PermissionDto;
import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permission")
@Slf4j
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/create")
    public ResponseEntity<Permission> createPermission(@RequestBody PermissionDto permissionDto) {
        Permission permission = permissionService.createPermission(
            permissionDto.getName(),
            permissionDto.getDescription(),
            permissionDto.getPermissionGroupId()
        );
        return ResponseEntity.status(201).body(permission);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long id, @RequestBody PermissionDto permissionDto) {
        Permission permission = permissionService.updatePermission(
            id,
            permissionDto.getName(),
            permissionDto.getDescription(),
            permissionDto.getPermissionGroupId()
        );
        return ResponseEntity.ok(permission);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @PostMapping("/by-ids")
    public ResponseEntity<Set<Permission>> getPermissionsByIds(@RequestBody Set<Long> ids) {
        return ResponseEntity.ok(permissionService.getPermissionsByIds(ids));
    }
}
