package com.people.hub.policyenginecore.service;

import com.people.hub.policyenginecore.dto.RuleRequestDto;
import com.people.hub.policyenginecore.model.Policy;
import com.people.hub.policyenginecore.model.Rule;
import com.people.hub.policyenginecore.repository.RuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleRepo ruleRepo;
    private final PolicyService policyService;
    private final DecisionService decisionService;

    public Rule createRule(RuleRequestDto requestDto) {
        Policy policy = policyService.getPolicyById(requestDto.getPolicyId());

        decisionService.validateDecision(
                policy.getServiceCode(),
                policy.getContextCode(),
                requestDto.getDecision()
        );

        Rule rule = new Rule();
        rule.setPolicyId(policy.getId());
        rule.setAttributeIdentifier(requestDto.getAttributeIdentifier());
        rule.setPriority(requestDto.getPriority());
        rule.setConstantType(requestDto.getConstantType());
        rule.setConstantValue(requestDto.getConstantValue());
        rule.setOperator(requestDto.getOperator());
        rule.setActive(requestDto.isActive());
        rule.setDecision(requestDto.getDecision().toString());
        return ruleRepo.save(rule);
    }
}
