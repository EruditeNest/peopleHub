package com.people.hub.authorization.service;

import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.repository.PermissionRepo;
import com.people.hub.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepo permissionRepo;

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
}
