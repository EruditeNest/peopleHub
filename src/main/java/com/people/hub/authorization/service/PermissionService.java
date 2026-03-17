package com.people.hub.authorization.service;

import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.repository.PermissionRepo;
import com.people.hub.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepo permissionRepo;
    private final RolePermissionService rolePermissionService;

    public Permission createPermission(String name, String description, Long groupId) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setPermissionGroupId(groupId);
        return permissionRepo.save(permission);
    }

    public Permission updatePermission(Long id, String name, String description, Long groupId) {
        Permission permission = permissionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission", id));
        permission.setName(name);
        permission.setDescription(description);
        permission.setPermissionGroupId(groupId);
        return permissionRepo.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepo.findAll();
    }

    public Permission getPermissionById(Long id) {
        return permissionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Permission", id));
    }

    public Set<Permission> getPermissionsByIds(Set<Long> ids) {
        List<Permission> permissions = permissionRepo.findAllById(ids);

        Set<Long> foundIds = permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());

        Set<Long> missingIds = new HashSet<>(ids);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()) {
            log.info("Ids not found while getting permissions: {}", missingIds);
        }
        return new HashSet<>(permissions);
    }

    public Set<Permission> getPermissionsByPermissionGroupId(Long id) {
        return permissionRepo.findAllByPermissionGroupId(id);
    }

    public Set<Permission> getPermissionsByPermissionGroupIds(Set<Long> ids) {
        return permissionRepo.findAllByPermissionGroupIdIn(ids);
    }

    @Transactional
    public void deleteAllPermissionsById(Set<Long> ids) {
        rolePermissionService.deleteAllByPermissionIds(ids);
        permissionRepo.deleteAllById(ids);
    }

    @Transactional
    public void deletePermissionById(Long id) {
        rolePermissionService.deleteAllByPermissionId(id);
        permissionRepo.deleteById(id);
    }

    @Transactional
    public int deleteAllPermissionsByPermissionGroupIds(Set<Long> ids){
        Set<Long> permissionIds = getPermissionsByPermissionGroupIds(ids).stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
        rolePermissionService.deleteAllByPermissionIds(permissionIds);
        return permissionRepo.deleteByPermissionGroupIdIn(ids);
    }

    @Transactional
    public int deleteAllPermissionsByPermissionGroupId(Long id){
        Set<Long> permissionIds = getPermissionsByPermissionGroupId(id).stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
        rolePermissionService.deleteAllByPermissionIds(permissionIds);
        return permissionRepo.deleteByPermissionGroupId(id);
    }
}
