package com.people.hub.user.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private String username;

    private String email;

    private String password;

    private String confirmPassword;

    private String phoneNumber;

    private Set<Long> roleIds;

    private boolean isActive;
}
