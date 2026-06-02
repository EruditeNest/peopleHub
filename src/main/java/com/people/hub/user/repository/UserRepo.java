package com.people.hub.user.repository;

import com.people.hub.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    boolean existsByIdAndDeletedFalseAndActiveTrue(Long id);

    @Query("""
        SELECT u.id
        FROM User u
        WHERE u.id IN :ids
          AND u.deleted = false
          AND u.active = true
    """)
    Set<Long> getAllActiveUserIds(List<Long> ids);

    Page<User> findByIdIn(Collection<Long> userIds, Pageable pageable);
}
