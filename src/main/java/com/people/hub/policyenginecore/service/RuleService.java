package com.people.hub.policyenginecore.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.policyengineapi.enums.DataType;
import com.people.hub.policyengineapi.service.AttributeDefinition;
import com.people.hub.policyengineapi.service.DataSource;
import com.people.hub.policyenginecore.dto.RuleRequestDto;
import com.people.hub.policyenginecore.model.Policy;
import com.people.hub.policyenginecore.model.Rule;
import com.people.hub.policyenginecore.repository.RuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final RuleRepo ruleRepo;
    private final PolicyService policyService;
    private final DecisionService decisionService;
    private final DataSourceRegistry dataSourceRegistry;

    public Rule createRule(RuleRequestDto requestDto) {
        Policy policy = policyService.getPolicyById(requestDto.getPolicyId());

        decisionService.validateDecision(
                policy.getServiceCode(),
                policy.getDecisionCode(),
                requestDto.getDecision()
        );

        validateConstantType(policy.getServiceCode(), requestDto.getAttributeIdentifier(), requestDto.getConstantType());
        validateConstantValue(requestDto.getConstantType(), requestDto.getConstantValue());

        Rule rule = new Rule();
        rule.setPolicyId(policy.getId());
        rule.setAttributeIdentifier(requestDto.getAttributeIdentifier());
        rule.setPriority(requestDto.getPriority());
        rule.setConstantType(requestDto.getConstantType());
        rule.setConstantValue(requestDto.getConstantValue());
        rule.setOperator(requestDto.getOperator());
        rule.setActive(requestDto.isActive());
        rule.setDecision(requestDto.getDecision());
        return ruleRepo.save(rule);
    }

    public Rule updateRule(Long id, RuleRequestDto requestDto) {
        Rule rule = getRuleById(id);

        Policy policy = policyService.getPolicyById(requestDto.getPolicyId());

        decisionService.validateDecision(
                policy.getServiceCode(),
                policy.getDecisionCode(),
                requestDto.getDecision()
        );

        validateConstantType(policy.getServiceCode(), requestDto.getAttributeIdentifier(), requestDto.getConstantType());
        validateConstantValue(requestDto.getConstantType(), requestDto.getConstantValue());

        rule.setPolicyId(policy.getId());
        rule.setAttributeIdentifier(requestDto.getAttributeIdentifier());
        rule.setPriority(requestDto.getPriority());
        rule.setConstantType(requestDto.getConstantType());
        rule.setConstantValue(requestDto.getConstantValue());
        rule.setOperator(requestDto.getOperator());
        rule.setActive(requestDto.isActive());
        rule.setDecision(requestDto.getDecision());
        return ruleRepo.save(rule);
    }

    public Rule getRuleById(Long id){
        return ruleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Rule not found", id));
    }

    public List<Rule> getAllRulesByPolicyId(Long policyId){
        return ruleRepo.findAllByPolicyId(policyId);
    }

    public List<Rule> getAllActiveRulesByPolicyId(Long policyId) {
        return ruleRepo.findAllByPolicyIdAndActiveTrue(policyId);
    }

    public RestApiResponse deleteRuleById(Long id) {
        ruleRepo.deleteById(id);
        return RestApiResponse.success("Rule (" + id + ") deleted successfully!");
    }

    public RestApiResponse deleteRuleByPolicyId(Long id) {
        int rules = ruleRepo.deleteAllByPolicyId(id);
        return RestApiResponse.success("Rules for PolicyId (" + id + ") deleted successfully! Number of rules are:" + rules);
    }

    private void validateConstantType(String serviceCode, String attributeIdentifier, DataType type) {
        DataSource dataSource = dataSourceRegistry.getDataSource(serviceCode);
        AttributeDefinition attributeDefinition = dataSource.getAttributeByIdentifier(attributeIdentifier);
        if (!attributeDefinition.getDataType().equals(type)) {
            throw new IllegalArgumentException("Invalid constant datatype");
        }
    }

    private void validateConstantValue(DataType type, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Constant value cannot be null");
        }
        boolean valid = switch (type) {
            case STRING, ENUM -> true;
            case INTEGER -> {
                try {
                    Integer.parseInt(value);
                    yield true;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case LONG -> {
                try {
                    Long.parseLong(value);
                    yield true;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case DECIMAL -> {
                try {
                    new BigDecimal(value);
                    yield true;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case BOOLEAN -> value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            case DATETIME -> {
                try {
                    LocalDateTime.parse(value);
                    yield true;
                } catch (DateTimeParseException e) {
                    yield false;
                }
            }
        };
        if (!valid) {
            throw new IllegalArgumentException("Invalid constant value '" + value + "' for data type " + type);
        }
    }
}
