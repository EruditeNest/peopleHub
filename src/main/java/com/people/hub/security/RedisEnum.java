package com.people.hub.security;

public enum RedisEnum {
    RolePermissionKey_,
    UserRoleKey_,
    Role_,
    LockUser_;

    public static String userRoleKey(Long userId) {
        return UserRoleKey_.name() + userId;
    }

    public static String rolePermissionKey(Long roleId) {
        return RolePermissionKey_.name() + roleId;
    }

    public static String roleAuthority(String roleName) {
        return Role_.name() + roleName;
    }

    public static String userLockKey(Long userId) {
        return LockUser_.name() + userId;
    }
}
