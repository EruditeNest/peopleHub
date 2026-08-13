package com.people.hub.policyenginecore.repository;

import com.people.hub.policyenginecore.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepo extends JpaRepository<Policy, Long> {
}
