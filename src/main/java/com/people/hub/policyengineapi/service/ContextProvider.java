package com.people.hub.policyengineapi.service;

import java.util.Map;

public interface ContextProvider {

    String getService();

    Map<String, ContextDefinition> getContexts();
}
