package com.people.hub.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("auth")
public class AuthorizationEvaluator {

    private static final String SUPER_ADMIN = RedisEnum.Role_ + "SUPER_ADMIN";

    public boolean hasPermission(
            Authentication authentication,
            String permission) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        SUPER_ADMIN.equals(authority.getAuthority())
                                || permission.equals(authority.getAuthority())
                );
    }

    public boolean hasRole(
            Authentication authentication,
            String role) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        SUPER_ADMIN.equals(authority.getAuthority())
                                || (RedisEnum.Role_ + role).equals(authority.getAuthority())
                );
    }
}
