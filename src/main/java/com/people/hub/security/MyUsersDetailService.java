package com.people.hub.security;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.user.model.User;
import com.people.hub.user.repository.UserRepo;
import com.people.hub.user.repository.UserRoleRepo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyUsersDetailService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;
    private final RedisService redisService;
    private final AuthorityProvider authorityProvider;

    MyUsersDetailService(
        UserRepo userRepo,
        UserRoleRepo userRoleRepo,
        RedisService redisService,
        @Qualifier("dbAuthorityProvider") AuthorityProvider authorityProvider
    ) {
        this.userRepo = userRepo;
        this.userRoleRepo = userRoleRepo;
        this.redisService = redisService;
        this.authorityProvider = authorityProvider;
    }

    @Override
    public MyUserDetail loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("user", email));
        Set<String> authorities = redisService.loadAuthorities(user.getUserId());

        if(authorities.isEmpty()) {
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
        User finalUser = user;
        user = userRepo.findByEmail(user.getEmail())
            .orElseThrow(() -> new NotFoundException("User", finalUser.getUserId().toString(), finalUser.getEmail()));
        Set<String> authorities = new HashSet<>();
        Map<String, String> roleIdNameForCache = new HashMap<>();

        Set<Long> respectiveRoleIds = userRoleRepo.findAllRoleIdsByUserId(user.getUserId());
        Map<Long, String> roleIdName = authorityProvider.getRoleIdNameById(respectiveRoleIds);
        Map<Long, Set<String>> permissionsByRoleId = authorityProvider.getPermissionsByRoleId(respectiveRoleIds);

        roleIdName.forEach((roleId, roleName) -> {
            Set<String> permissionForCache = new HashSet<>();
            authorities.add(RedisEnum.roleAuthority(roleName));

            roleIdNameForCache.put(String.valueOf(roleId), roleName);

            permissionsByRoleId.get(roleId).forEach((permissionName) -> {
                authorities.add(permissionName);
                permissionForCache.add(permissionName);
            });
            redisService.cacheRolePermissions(roleId, permissionForCache);
        });

        redisService.cacheUserRoles(user.getUserId(), roleIdNameForCache);
        return authorities;
    }
}
