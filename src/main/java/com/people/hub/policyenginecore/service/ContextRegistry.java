package com.people.hub.policyenginecore.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.policyengineapi.service.ContextDefinition;
import com.people.hub.policyengineapi.service.ContextProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContextRegistry {

    private final Map<String, ContextProvider> contexts;

    public ContextRegistry(List<ContextProvider> providers) {

        contexts = providers.stream()
                .collect(Collectors.toMap(
                        ContextProvider::getService,
                        Function.identity()
                ));
    }

    public ContextProvider getContextProvider(String service) {
        return contexts.get(service);
    }

    public ContextDefinition getContextDefinition(String service, String code) {
        ContextProvider provider = contexts.get(service);
        if (provider == null) {
            throw new NotFoundException(
                    "Context provider not found",
                    service
            );
        }
        ContextDefinition definition = provider.getContexts().get(code);
        if (definition == null) {
            throw new NotFoundException(
                    "Context definition not found",
                    service + ":" + code
            );
        }
        return definition;
    }
}
