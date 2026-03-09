package com.people.hub.user.repository;

import com.people.hub.user.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long> {

    Set<UserRole> findAllByUserId(Long userId);

    Set<UserRole> findAllByRoleId(Long roleId);

    @Query("""
        SELECT DISTINCT(urm.roleId)
        FROM UserRoleMapping urm
        WHERE urm.userId = :userId;
    """)
    Set<Long> findAllRoleIdsByUserId(@Param("userId") Long userId);
}
