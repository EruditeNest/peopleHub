package com.people.hub.security;

import com.people.hub.authorization.service.PermissionService;
import com.people.hub.core.user.User;
import com.people.hub.core.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyUsersDetailService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PermissionService permissionService;

    @Override
    public MyUserDetail loadUserByUsername(String email) throws UsernameNotFoundException {

        // when implement redis use lazy loading.

        User user = userRepo.findByEmailWithRolesAndPermissions(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            role.getPermissions().forEach(permission ->
                authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        });

        return new MyUserDetail(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }
}
