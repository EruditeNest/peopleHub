package com.people.hub.leavemanagement.service;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.service.DataSource;
import com.people.hub.policyengineapi.service.Decision;

import java.util.List;
import java.util.Map;

public class LeaveDataSource implements DataSource {
    @Override
    public Map<AttributeIdentifier, List<AttributeIdentifier>> listSupportedAttributes() {
        return Map.of();
    }

    @Override
    public Map<AttributeIdentifier, Object> getAttributes(List<AttributeIdentifier> attributes, Map<AttributeIdentifier, Object> dependencies) {
        return Map.of();
    }

    @Override
    public Decision getDecisionByCode(String decisionCode) {
        return null;
    }
}
