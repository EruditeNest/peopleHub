package com.people.hub.authorization.service;

import com.people.hub.authorization.dto.RoleDto;
import com.people.hub.authorization.dto.RolePermissionResponse;
import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.model.PermissionGroup;
import com.people.hub.authorization.model.Role;
import com.people.hub.authorization.repository.RoleRepo;
import com.people.hub.common.RestApiResponse;
import com.people.hub.common.dto.PageInfo;
import com.people.hub.common.exception.ForbiddenException;
import com.people.hub.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepo roleRepo;
    private final PermissionService permissionService;
    private final PermissionGroupService permissionGroupService;
    private final RolePermissionService rolePermissionService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "created_at", "updated_at");

    @Transactional
    public Role createRole(RoleDto roleDto) {
        log.info("Creating role: {}", roleDto.getName());

        if(roleRepo.existsByName(roleDto.getName())) {
            throw new ForbiddenException("Role already exists: " + roleDto.getName());
        }

        Role role = new Role();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        role.setCreatedBy(roleDto.getCreatedBy());
        role.setUpdatedBy(roleDto.getUpdatedBy());
        Role savedRole = roleRepo.save(role);
        rolePermissionService.createRolePermissionMapping(savedRole.getId(), roleDto.getPermissionIds(), roleDto.getCreatedBy());
        return savedRole;
    }

    @Transactional
    public Role updateRole(Long id, RoleDto roleDto) {
        log.info("update role initiated for Id: {}", id);
        Role role = roleRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Role", id));
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        role.setUpdatedBy(roleDto.getUpdatedBy());
        Role savedRole = roleRepo.save(role);
        rolePermissionService.updateRolePermissionMapping(
            savedRole.getId(),
            roleDto.getPermissionIds(),
            roleDto.getCreatedBy(),
            roleDto.getUpdatedBy()
        );
        return savedRole;
    }

    public Role getRoleById(Long id) {
        log.info("getRoleById with id: {}", id);
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));
        return role;
    }

    public RolePermissionResponse getRoleByIdWithPermissions(Long id) {
        log.info("getRoleByIdWithPermission with id: {}", id);
        Role role = roleRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Role", id));

        Set<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(id);
        Set<Permission> respectivePermissions = permissionService.getPermissionsByIds(permissionIds);

        Map<Long, Set<Permission>> permissionByGroup = respectivePermissions.stream()
            .collect(Collectors.groupingBy(
                Permission::getPermissionGroupId,
                Collectors.toSet()
            ));

        Set<Long> permissionGroupIds = respectivePermissions.stream()
                .map(Permission::getPermissionGroupId)
                .collect(Collectors.toSet());
        Set<PermissionGroup> permissionGroups = new HashSet<>(permissionGroupService.getAllPermissionGroupsById(permissionGroupIds));
        return new RolePermissionResponse(role, permissionByGroup, permissionGroups);
    }

    public RoleDto getRoleByIdWithPermissionIds(Long id) {
        log.info("getRoleByIdWithPermissionIds with id: {}", id);
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));
        Set<Long> permissionIds = rolePermissionService.getPermissionIdsByRoleId(id);
        RoleDto roleDto = new RoleDto(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permissionIds,
                role.getCreatedBy(),
                role.getUpdatedBy(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
        return roleDto;
    }

    public RestApiResponse getAllRoles(int page, int size, String sortField, String sortOrder) {
        log.info("getAllRoles with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "created_at";
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Role> roles = roleRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(roles.getNumber(), roles.getSize(), roles.getTotalElements());
        return RestApiResponse.success(pageInfo, roles.getContent());
    }

    public RestApiResponse getAllRolesWithPermissionIds(int page, int size, String sortField, String sortOrder) {
        log.info("getAllRolesWithPermissionIds with page: {}, size: {}, sortField: {}, sortOrder: {}", page, size, sortField, sortOrder);
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "created_at";
        }
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Role> roles = roleRepo.findAll(pageable);
        PageInfo pageInfo = new PageInfo(roles.getNumber(), roles.getSize(), roles.getTotalElements());

        if (roles.isEmpty()) {
            return RestApiResponse.success(pageInfo, Collections.emptyList());
        }

        Set<Long> roleIds = roles.getContent()
                .stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        Map<Long, Set<Long>> roleIdPermissionIdsMap = rolePermissionService.getPermissionIdsByRoleIds(roleIds);

        Page<RoleDto> roleDtoPage = roles.map(role ->
            new RoleDto(
                    role.getId(),
                    role.getName(),
                    role.getDescription(),
                    roleIdPermissionIdsMap.getOrDefault(role.getId(), Collections.emptySet()),
                    role.getCreatedBy(),
                    role.getUpdatedBy(),
                    role.getCreatedAt(),
                    role.getUpdatedAt()
            )
        );

        return RestApiResponse.success(pageInfo, roleDtoPage.getContent());
    }

    @Transactional
    public RestApiResponse deleteRole(Long id) {
        log.info("Delete Role by id: {}", id);

        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role", id));

        roleRepo.delete(role);
        rolePermissionService.deleteAllByRoleId(id);
        return RestApiResponse.success("Role deleted successfully");
    }
}
