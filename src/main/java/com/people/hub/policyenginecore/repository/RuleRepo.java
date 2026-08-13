package com.people.hub.policyenginecore.repository;

import com.people.hub.policyenginecore.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepo extends JpaRepository<Rule, Long> {
}
