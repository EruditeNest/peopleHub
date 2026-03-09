package com.people.hub.authorization.dto;

import lombok.Data;

@Data
public class PermissionDto {
    private String name;
    private String description;
    private Long permissionGroupId;
}
