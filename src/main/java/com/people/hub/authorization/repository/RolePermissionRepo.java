package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepo extends JpaRepository<RolePermission, Long> {

    boolean existsByRoleId(Long roleId);

    @Modifying
    int deleteAllByRoleId(Long roleId);

    @Modifying
    int deleteByRoleIdAndPermissionIdIn(Long roleId, Set<Long> permissionIds);

    @Query("""
        SELECT DISTINCT rp.permissionId
        FROM RolePermission rp
        WHERE rp.roleId = :roleId
    """)
    Set<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Query("""
        SELECT rpm.roleId, p.name
        FROM RolePermissionMapping rpm
        JOIN Permission p ON p.id = rpm.permissionId
        WHERE rpm.roleId IN :roleIds
    """)
    List<Object[]> findRolePermissions(@Param("roleIds") Set<Long> roleIds);
}
