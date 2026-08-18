package com.people.hub.policyengineapi.service;

import com.people.hub.policyengineapi.dto.DecisionDefinition;

import java.util.Collection;

public interface DecisionDefinitionProvider {

    String getService();

    boolean supports(String decisionCode);

    DecisionDefinition getDefinition(String code);

    Collection<DecisionDefinition> getDefinitions();
}
