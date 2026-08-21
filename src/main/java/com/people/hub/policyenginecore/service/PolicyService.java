package com.people.hub.policyenginecore.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.policyengineapi.dto.DecisionDefinition;
import com.people.hub.policyengineapi.service.ContextDefinition;
import com.people.hub.policyengineapi.service.ModuleDefinition;
import com.people.hub.policyenginecore.dto.PolicyRequestDto;
import com.people.hub.policyenginecore.model.Policy;
import com.people.hub.policyenginecore.repository.PolicyRepo;
import com.people.hub.policyenginecore.repository.RuleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepo policyRepo;
    private final RuleRepo ruleRepo;
    private final ModuleRegistry moduleRegistry;
    private final ContextRegistry contextRegistry;
    private final DecisionDefinitionRegistry decisionDefinitionRegistry;

    public Policy createPolicy(PolicyRequestDto requestDto) {
        validateServiceCode(requestDto.getServiceCode());
        validateDecisionCode(requestDto.getServiceCode(), requestDto.getDecisionCode());
        validateContextCode(requestDto.getServiceCode(), requestDto.getContextCode());

        Policy policy = new Policy();
        policy.setName(requestDto.getName());
        policy.setServiceCode(requestDto.getServiceCode());
        policy.setContextCode(requestDto.getContextCode());
        policy.setDecisionCode(requestDto.getDecisionCode());
        policy.setRuleCombinationStrategy(requestDto.getRuleCombinationStrategy());
        policy.setActive(requestDto.isActive());
        return policyRepo.save(policy);
    }


    public Policy updatePolicy(Long id, PolicyRequestDto requestDto) {
        Policy policy = getPolicyById(id);

        validateServiceCode(requestDto.getServiceCode());
        validateDecisionCode(requestDto.getServiceCode(), requestDto.getDecisionCode());
        validateContextCode(requestDto.getServiceCode(), requestDto.getContextCode());

        policy.setName(requestDto.getName());
        policy.setServiceCode(requestDto.getServiceCode());
        policy.setContextCode(requestDto.getContextCode());
        policy.setDecisionCode(requestDto.getDecisionCode());
        policy.setRuleCombinationStrategy(requestDto.getRuleCombinationStrategy());
        policy.setActive(requestDto.isActive());
        return policyRepo.save(policy);
    }

    public Policy getPolicyById(Long id){
        return policyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found", id));
    }

    public List<Policy> getPolicyByServiceCode(String serviceCode) {
        return policyRepo.findAllByServiceCode(serviceCode);
    }

    public List<Policy> getPolicyByContextCode(String contextCode) {
        return policyRepo.findAllByContextCode(contextCode);
    }

    public List<Policy> getPolicyByDecisionCode(String decisionCode) {
        return policyRepo.findAllByDecisionCode(decisionCode);
    }

    public RestApiResponse deletePolicyById(Long id) {
        policyRepo.deleteById(id);
        ruleRepo.deleteAllByPolicyId(id);
        return RestApiResponse.success("Policy and rules under policyId: "+ id + " deleted successfully!");
    }

    private void validateServiceCode(String serviceCode){
        ModuleDefinition moduleDefinition = moduleRegistry.getModuleDefinition(serviceCode);
        if(moduleDefinition == null) {
            throw new IllegalArgumentException("Invalid module: " + serviceCode);
        }
    }

    private void validateContextCode(String serviceCode, String contextCode){
        ContextDefinition contextDefinition = contextRegistry.getContextDefinition(serviceCode, contextCode);
        if(contextDefinition == null) {
            throw new IllegalArgumentException("Invalid Context: " + contextCode);
        }
    }

    private void validateDecisionCode(String serviceCode, String decisionCode){
        DecisionDefinition decisionDefinition = decisionDefinitionRegistry.getDefinition(serviceCode, decisionCode);
        if(decisionDefinition == null) {
            throw new IllegalArgumentException("Invalid decision: " + decisionCode);
        }
    }
}
