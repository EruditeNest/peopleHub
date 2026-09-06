package com.people.hub.policyenginecore.service;

import com.people.hub.policyengineapi.dto.DecisionDefinition;
import com.people.hub.policyengineapi.service.DecisionDefinitionProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DecisionDefinitionRegistry {

    private final Map<String, DecisionDefinitionProvider> providers;

    public DecisionDefinitionRegistry(List<DecisionDefinitionProvider> dataSources) {
        this.providers = dataSources.stream()
                .collect(Collectors.toMap(
                        DecisionDefinitionProvider::getService,
                        Function.identity()
                ));
    }

    // get DecisionDefinition based on the context.
    public DecisionDefinition getDefinition(String service, String decisionCode) {
        return providers.get(service).getDefinition(decisionCode);
    }
}
