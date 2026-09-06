package com.people.hub.leavemanagement.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.leavemanagement.enums.LeaveAttributeDefinition;
import com.people.hub.leavemanagement.enums.LeaveModuleDefinition;
import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.service.AttributeDefinition;
import com.people.hub.policyengineapi.service.DataSource;
import com.people.hub.policyengineapi.service.ModuleDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LeaveDataSource implements DataSource {
    private final Map<String, AttributeDefinition> attributes;

    public LeaveDataSource() {
        this.attributes = Arrays.stream(LeaveAttributeDefinition.values())
                .collect(Collectors.toMap(
                        LeaveAttributeDefinition::getIdentifierString,
                        Function.identity()
                ));
    }

    @Override
    public Map<AttributeIdentifier, List<AttributeIdentifier>> listSupportedAttributes() {
        return Map.of();
    }

    @Override
    public Map<AttributeIdentifier, Object> getAttributes(List<AttributeIdentifier> attributes, Map<AttributeIdentifier, Object> dependencies) {
        return Map.of();
    }

    @Override
    public AttributeDefinition getAttributeByIdentifier(String identifier){
        AttributeDefinition definition = attributes.get(identifier);
        if (definition == null) {
            throw new NotFoundException("Attribute not found", identifier);
        }
        return definition;
    }

    @Override
    public List<AttributeDefinition> getAllAttributeDefinitions() {
        return attributes.values().stream().toList();
    }

    @Override
    public ModuleDefinition getModule() {
        return LeaveModuleDefinition.INSTANCE;
    }
}
