package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {
    @Query("""
        SELECT p.name FROM RolePermission rp
        JOIN rp.permission p
        WHERE rp.role.id = :roleId
    """)
    Set<String> findPermissionNamesByRoleId(@Param("roleId") Long roleId);
}
