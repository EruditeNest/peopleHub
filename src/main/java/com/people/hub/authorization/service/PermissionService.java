package com.people.hub.authorization.service;

import com.people.hub.authorization.repository.PermissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepo permissionRepo;

    public Set<String> getPermissionNamesByRoleId(Long roleId) {
        return permissionRepo.findPermissionNamesByRoleId(roleId);
    }
}
