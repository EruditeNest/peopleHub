package com.people.hub.authorization.controller;

import com.people.hub.authorization.dto.RoleDto;
import com.people.hub.authorization.model.Role;
import com.people.hub.authorization.service.RoleService;
import com.people.hub.core.common.RestApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
@Slf4j
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/create-role")
    public ResponseEntity<Role> createRole(@RequestBody RoleDto roleDto) {
        log.info("Post Request: create role");
        Role role = roleService.createRole(roleDto);
        return ResponseEntity.status(201).body(role);
    }

    @PostMapping("/update-role")
    public ResponseEntity<Role> updateRole(@RequestBody RoleDto roleDto) {
        log.info("Post Request: update role for id: {}", roleDto.getRoleId());
        Role role = roleService.updateRole(roleDto.getRoleId(), roleDto);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        log.info("Get Request: get role for id: {}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/with-permissions/{id}")
    public ResponseEntity<Role> getRoleByIdWithPermissions(@PathVariable Long id) {
        log.info("Get Request: get role with permissions for id: {}", id);
        return ResponseEntity.ok(roleService.getRoleByIdWithPermissions(id));
    }

    @GetMapping("/with-permission_ids/{id}")
    public ResponseEntity<RoleDto> getRoleByIdWithPermissionIds(@PathVariable Long id) {
        log.info("Get Request: get role with permission ids for id: {}", id);
        return ResponseEntity.ok(roleService.getRoleByIdWithPermissionIds(id));
    }

    @GetMapping("/all")
    public RestApiResponse getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        log.info("Get Request: get all roles for page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        return roleService.getAllRoles(page, size, sortField, sortOrder);
    }

    @GetMapping("/all-with-permission_ids")
    public RestApiResponse getAllRolesWithPermissionIds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        log.info("Get Request: get all roles with permission ids for page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        return roleService.getAllRolesWithPermissionIds(page, size, sortField, sortOrder);
    }

    @GetMapping("/delete/{id}")
    public RestApiResponse deleteRole(@PathVariable Long id) {
        log.info("Get Request: delete role for id: {}", id);
        return roleService.deleteRole(id);
    }
}
