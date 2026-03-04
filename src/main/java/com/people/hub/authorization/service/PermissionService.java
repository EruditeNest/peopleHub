package com.people.hub.authorization.service;

import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.repository.PermissionRepo;
import com.people.hub.core.common.exception.BadRequestException;
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

    public Set<String> getPermissionNamesByRoleId(Long roleId) {
        return permissionRepo.findPermissionNamesByRoleId(roleId);
    }

    public Set<Long> getPermissionIdsByRoleId(Long roleId) {
        return permissionRepo.findPermissionIdsByRoleId(roleId);
    }

    public Permission getPermissionById(Long id) {
        return permissionRepo.findById(id)
            .orElseThrow(() -> new BadRequestException("no permission found for Id: " + id));
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

    public Map<Long, Set<Long>> mapPermissionsIdsByRoleIds(Set<Long> roleIds) {
        List<Object[]> results = permissionRepo.findPermissionIdsByRoleIds(roleIds);

        Map<Long, Set<Long>> permissionMap = new HashMap<>();
        for (Object[] row : results) {
            Long roleId = (Long) row[0];
            Long permissionId = (Long) row[1];

            permissionMap
                    .computeIfAbsent(roleId, k -> new HashSet<>())
                    .add(permissionId);
        }
        return permissionMap;
    }
}
