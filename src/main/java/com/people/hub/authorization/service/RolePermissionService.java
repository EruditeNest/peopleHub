package com.people.hub.authorization.service;

import com.people.hub.authorization.model.RolePermission;
import com.people.hub.authorization.repository.RolePermissionRepo;
import com.people.hub.common.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepo rolePermissionRepo;

    public List<RolePermission> createRolePermissionMapping(Long roleId, Set<Long> permissionIds, Long createdBy) {
        if(rolePermissionRepo.existsByRoleId(roleId)) {
            throw new ForbiddenException("RoleId already exists in mapping: " + roleId);
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        List<RolePermission> mappings = permissionIds.stream()
            .map(permissionId -> buildRolePermission(roleId, permissionId, createdBy, createdBy))
            .toList();

        return rolePermissionRepo.saveAll(mappings);
    }

    public List<RolePermission> updateRolePermissionMapping(Long roleId, Set<Long> permissionIds, Long createdBy, Long updatedBy) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            rolePermissionRepo.deleteAllByRoleId(roleId);
            return List.of();
        }
        Set<Long> existingPermissionIds = rolePermissionRepo.findPermissionIdsByRoleId(roleId);

        deleteFromExistingMapping(roleId, existingPermissionIds, permissionIds);

        Set<Long> toAdd = new HashSet<>(permissionIds);
        toAdd.removeAll(existingPermissionIds);

        if (toAdd.isEmpty()) {
            return List.of();
        }
        List<RolePermission> mappings = toAdd.stream()
            .map(permissionId -> buildRolePermission(roleId, permissionId, createdBy, updatedBy))
            .toList();

        return rolePermissionRepo.saveAll(mappings);
    }

    public Set<Long> getPermissionIdsByRoleId(Long roleId) {
        return rolePermissionRepo.findPermissionIdsByRoleId(roleId);
    }

    public Map<Long, Set<Long>> getPermissionIdsByRoleIds(Set<Long> roleIds) {
        if(roleIds == null || roleIds.isEmpty()) {
            throw new ForbiddenException("Invalid roleIds: " + roleIds);
        }

        List<RolePermission> mappings = rolePermissionRepo.findByRoleIdIn(roleIds);

        return mappings.stream()
            .collect(Collectors.groupingBy(
                RolePermission::getRoleId,
                Collectors.mapping(RolePermission::getPermissionId, Collectors.toSet())
            ));
    }

    public int deleteAllByRoleId(Long roleId) {
        return rolePermissionRepo.deleteAllByRoleId(roleId);
    }

    private void deleteFromExistingMapping(Long roleId, Set<Long> existingPermissionIds, Set<Long> permissionIds) {
        Set<Long> toDelete = new HashSet<>(existingPermissionIds);
        toDelete.removeAll(permissionIds);

        if (!toDelete.isEmpty()) {
            rolePermissionRepo.deleteByRoleIdAndPermissionIdIn(roleId, toDelete);
        }
    }

    private RolePermission buildRolePermission(Long roleId, Long permissionId, Long createdBy, Long updatedBy) {
        RolePermission mapping = new RolePermission();
        mapping.setRoleId(roleId);
        mapping.setPermissionId(permissionId);
        mapping.setCreatedBy(createdBy);
        mapping.setUpdatedBy(updatedBy);
        return mapping;
    }
}
