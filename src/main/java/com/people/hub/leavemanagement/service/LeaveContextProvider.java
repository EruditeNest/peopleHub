package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.enums.LeaveContextDefinition;
import com.people.hub.policyengineapi.service.ContextDefinition;
import com.people.hub.policyengineapi.service.ContextProvider;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LeaveContextProvider implements ContextProvider {
    @Override
    public String getService() {
        return "leave";
    }

    @Override
    public Map<String, ContextDefinition> getContexts() {
        return Arrays.stream(LeaveContextDefinition.values())
                .collect(Collectors.toMap(
                        LeaveContextDefinition::getCode,
                        Function.identity()
                ));
    }
}
