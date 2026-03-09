package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {

    @Query("""
        SELECT r.id, p.id
        FROM Role r
        JOIN r.permissions p
        WHERE r.id IN :roleIds
    """)
    List<Object[]> findPermissionIdsByRoleIds(@Param("roleIds") Set<Long> roleIds);

}
