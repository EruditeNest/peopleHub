package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.enums.LeaveModuleDefinition;
import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.service.DataSource;
import com.people.hub.policyengineapi.service.ModuleDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
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
    public ModuleDefinition getModule() {
        return LeaveModuleDefinition.INSTANCE;
    }
}
