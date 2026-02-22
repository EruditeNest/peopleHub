package com.people.hub.security;

import com.people.hub.core.common.exception.BadRequestException;
import com.people.hub.core.user.User;
import com.people.hub.core.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyUsersDetailService implements UserDetailsService {

    private final UserRepo userRepo;
    private final RedisService redisService;

    @Override
    public MyUserDetail loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new BadRequestException("User not found"));
        Set<String> authorities = redisService.loadAuthorities(user.getUserId());

        if(authorities.isEmpty()) {
            user = userRepo.findByEmailWithRolesAndPermissions(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            authorities = rebuildWithLock(user);
        }

        return new MyUserDetail(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }

    private Set<String> rebuildWithLock(User user) {
        Boolean acquired = redisService.lockRedisUpdateByUserId(user.getUserId());
        if (Boolean.TRUE.equals(acquired)) {
            try {
                // Double check after acquiring lock
                Set<String> cached = redisService.loadAuthorities(user.getUserId());
                if (!cached.isEmpty()) {
                    return cached;
                }
                user = userRepo.findByEmailWithRolesAndPermissions(user.getEmail())
                    .orElseThrow(() -> new BadRequestException("User not found!!!"));
                return getAuthoritiesFromDb(user);
            } finally {
                redisService.deleteLockKeyByUserId(user.getUserId());
            }
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
            return redisService.loadAuthorities(user.getUserId());
        }
    }

    private Set<String> getAuthoritiesFromDb(User user) {
        Set<String> authorities = new HashSet<>();

        Map<String, String> roleIdNameForCache = new HashMap<>();
        user.getRoles().forEach(role -> {
            Set<String> permissionForCache = new HashSet<>();
            authorities.add(RedisEnum.roleAuthority(role.getName()));

            roleIdNameForCache.put(String.valueOf(role.getId()), role.getName());

            role.getPermissions().forEach(permission -> {
                authorities.add(permission.getName());
                permissionForCache.add(permission.getName());
            });
            redisService.cacheRolePermissions(role.getId(), permissionForCache);
        });

        redisService.cacheUserRoles(user.getUserId(), roleIdNameForCache);
        return authorities;
    }
}
