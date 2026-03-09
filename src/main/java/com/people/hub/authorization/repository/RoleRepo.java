package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    boolean existsByName(String name);
}
