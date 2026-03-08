package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.UserRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleMappingRepo extends JpaRepository<UserRoleMapping, Long> {
}
