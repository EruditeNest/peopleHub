package com.people.hub.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private static final Duration CACHE_TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;

    public void cacheUserRoles(Long userId, Map<String, String> roleIdName) {
        String key = RedisEnum.userRoleKey(userId);

        redisTemplate.opsForHash().putAll(key, roleIdName);
        redisTemplate.expire(key, CACHE_TTL);
    }

    public void cacheRolePermissions(Long roleId, Set<String> permissions) {
        String key = RedisEnum.rolePermissionKey(roleId);

        String[] permissionsList = permissions.toArray(String[]::new);

        redisTemplate.opsForSet().add(key, permissionsList);
        redisTemplate.expire(key, CACHE_TTL);
    }

    public void clearCacheForRoleId(Long roleId) {
        String key = RedisEnum.rolePermissionKey(roleId);
        redisTemplate.delete(key);
    }

    public void clearCacheForUserId(Long userId) {
        String key = RedisEnum.userRoleKey(userId);
        redisTemplate.delete(key);
    }

    public void deleteLockKeyByUserId(Long userId) {
        String key = RedisEnum.userLockKey(userId);
        redisTemplate.delete(key);
    }

    public Boolean lockRedisUpdateByUserId(Long userId) {

        String lockKey = RedisEnum.userLockKey(userId);

        return redisTemplate.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(5));
    }

    public Set<String> loadAuthorities(Long userId) {

        Set<String> authorities = new HashSet<>();
        String roleKey = RedisEnum.userRoleKey(userId);
        Map<Object, Object> roles = redisTemplate.opsForHash().entries(roleKey);

        if (roles.isEmpty()) {
            log.info("No cached roles found for userId: {}", userId);
            return authorities;
        }
        for (Map.Entry<Object, Object> role : roles.entrySet()) {
            String roleId = role.getKey().toString();
            String roleName = role.getValue().toString();

            authorities.add(RedisEnum.roleAuthority(roleName));

            String permissionKey = RedisEnum.rolePermissionKey(Long.valueOf(roleId));
            Set<String> permissions = redisTemplate.opsForSet().members(permissionKey);

            if (permissions == null || permissions.isEmpty()) {
                log.info("Permission cache incomplete for roleId: {}", roleId);
                return new HashSet<>();
            }
            authorities.addAll(permissions);
            redisTemplate.expire(permissionKey, CACHE_TTL);
        }
        redisTemplate.expire(roleKey, CACHE_TTL);
        return authorities;
    }
}
