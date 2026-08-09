package com.people.hub.policyenginecore.service;

import com.people.hub.policyengineapi.service.ContextDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContextRegistry {

    private final Map<String, ContextDefinition> contexts;

    public ContextRegistry(List<ContextDefinition> definitions) {

        contexts = definitions.stream()
                .collect(Collectors.toMap(
                        ContextDefinition::getCode,
                        Function.identity()
                ));
    }

    public ContextDefinition get(String code) {
        return contexts.get(code);
    }
}
