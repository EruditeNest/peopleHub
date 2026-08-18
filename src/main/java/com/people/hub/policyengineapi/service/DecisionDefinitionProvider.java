package com.people.hub.policyengineapi.service;

import com.people.hub.policyengineapi.dto.DecisionDefinition;

import java.util.Collection;

public interface DecisionDefinitionProvider extends ModuleComponent {

    default String getService() {
        return getModule().getCode();
    }

    boolean supports(String decisionCode);

    DecisionDefinition getDefinition(String code);

    Collection<DecisionDefinition> getDefinitions();
}
