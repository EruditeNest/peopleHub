package com.people.hub.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
        SELECT DISTINCT u FROM User u
        JOIN FETCH u.roles r
        JOIN FETCH r.permissions
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

    boolean existsByEmail(String email);
}
