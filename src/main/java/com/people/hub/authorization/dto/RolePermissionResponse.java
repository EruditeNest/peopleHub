package com.people.hub.authorization.dto;

import com.people.hub.authorization.model.Permission;
import com.people.hub.authorization.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    private Role role;
    private Set<Permission> permissions;
}
