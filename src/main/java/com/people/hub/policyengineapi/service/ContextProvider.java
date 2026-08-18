package com.people.hub.policyengineapi.service;

import java.util.Map;

public interface ContextProvider extends ModuleComponent {

    default String getService() {
        return getModule().getCode();
    }

    Map<String, ContextDefinition> getContexts();
}
