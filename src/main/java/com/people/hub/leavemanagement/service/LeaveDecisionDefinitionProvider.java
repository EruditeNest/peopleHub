package com.people.hub.leavemanagement.service;

import com.people.hub.policyengineapi.dto.DecisionDefinition;
import com.people.hub.policyengineapi.service.Decision;
import com.people.hub.policyengineapi.service.DecisionDefinitionFactory;
import com.people.hub.policyengineapi.service.DecisionDefinitionProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LeaveDecisionDefinitionProvider implements DecisionDefinitionProvider {

    private final Map<String, DecisionDefinition> definitions;

    public LeaveDecisionDefinitionProvider(
        List<Decision> decisions,
        DecisionDefinitionFactory factory) {

            this.definitions = decisions.stream()
                    .collect(Collectors.toMap(
                            Decision::getCode,
                            factory::create
                    ));
    }

    @Override
    public String getService() {
        return "leave";
    }

    @Override
    public boolean supports(String decisionCode) {
        return definitions.containsKey(decisionCode);
    }

    @Override
    public DecisionDefinition getDefinition(String code) {
        return definitions.get(code);
    }

    @Override
    public Collection<DecisionDefinition> getDefinitions() {
        return definitions.values();
    }
}
