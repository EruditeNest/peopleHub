package com.people.hub.user.repository;

import com.people.hub.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
