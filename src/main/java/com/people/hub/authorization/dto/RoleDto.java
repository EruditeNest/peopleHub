package com.people.hub.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long roleId;

    private String name;

    private String description;

    private Set<Long> permissionIds;

    private Long createdBy;

    private Long updatedBy;

    private Instant createdAt;

    private Instant updatedAt;
}
