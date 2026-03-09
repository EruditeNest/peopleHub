package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionGroupRepo extends JpaRepository<PermissionGroup, Long> {
}
