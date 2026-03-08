package com.people.hub.authorization.repository;

import com.people.hub.authorization.model.RolePermissionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionMappingRepo extends JpaRepository<RolePermissionMapping, Long> {
}
