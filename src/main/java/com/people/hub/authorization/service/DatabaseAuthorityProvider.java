package com.people.hub.authorization.service;

import com.people.hub.authorization.model.Role;
import com.people.hub.authorization.repository.RolePermissionRepo;
import com.people.hub.authorization.repository.RoleRepo;
import com.people.hub.security.AuthorityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("dbAuthorityProvider")
@RequiredArgsConstructor
public class DatabaseAuthorityProvider implements AuthorityProvider {

    private final RoleRepo roleRepo;
    private final RolePermissionRepo rolePermissionRepo;

    @Override
    public Map<Long, String> getRoleIdNameById(Set<Long> roleIds) {
        List<Role> roles = roleRepo.findAllById(roleIds);
        Map<Long, String> roleIdNameMap = new HashMap<>();
        roles.forEach(r ->
            roleIdNameMap.put(r.getId(), r.getName())
        );
        return roleIdNameMap;
    }

    @Override
    public Map<Long, Set<String>> getPermissionsByRoleId(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> rows = rolePermissionRepo.findRolePermissions(roleIds);
        Map<Long, Set<String>> result = new HashMap<>();
        for (Object[] row : rows) {
            Long roleId = (Long) row[0];
            String permissionName = (String) row[1];
            result.computeIfAbsent(roleId, k -> new HashSet<>()).add(permissionName);
        }

        return result;
    }
}
