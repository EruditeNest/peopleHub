package com.people.hub.security;

import com.people.hub.authorization.service.PermissionService;
import com.people.hub.core.user.User;
import com.people.hub.core.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyUsersDetailService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PermissionService permissionService;

    @Override
    public MyUserDetail loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = Optional.of(userRepo.findByEmail(email))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<String> permissions = permissionService.getPermissionNamesByRoleId(user.getRoleId());

        Collection<? extends GrantedAuthority> authorities =
            permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new MyUserDetail(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getRoleId(),
            authorities
        );
    }
}
