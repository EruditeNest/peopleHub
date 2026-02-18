package com.people.hub.security;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
public class MyUserDetail implements UserDetails {

    private final Long userId;

    private final String username;

    private final String email;

    private final String password;

    private final Long roleId;

    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public MyUserDetail(Long userId, String username, String email, String password, Long roleId, Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleId = roleId;
    }

    public MyUserDetail(Long userId, String username, String email, String password, Long roleId, Set<String> authorities) {
        this.authorities = getAuthoritiesFromStringSet(authorities);
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleId = roleId;
    }

    public static Collection<? extends GrantedAuthority> getAuthoritiesFromStringSet(Set<String> permissions) {
        return permissions.stream()
            .map(SimpleGrantedAuthority::new)
            .toList();
    }

}
