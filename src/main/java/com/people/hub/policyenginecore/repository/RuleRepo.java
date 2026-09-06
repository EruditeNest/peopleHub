package com.people.hub.policyenginecore.repository;

import com.people.hub.policyenginecore.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepo extends JpaRepository<Rule, Long> {

    List<Rule> findAllByPolicyId(Long policyId);

    List<Rule> findAllByPolicyIdAndActiveTrue(Long policyId);

    int deleteAllByPolicyId(Long policyId);
}
