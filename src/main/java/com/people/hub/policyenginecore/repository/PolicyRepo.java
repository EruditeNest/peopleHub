package com.people.hub.policyenginecore.repository;

import com.people.hub.policyenginecore.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepo extends JpaRepository<Policy, Long> {
    List<Policy> findAllByServiceCode(String serviceCode);

    List<Policy> findAllByContextCode(String contextCode);

    List<Policy> findAllByDecisionCode(String decisionCode);
}
