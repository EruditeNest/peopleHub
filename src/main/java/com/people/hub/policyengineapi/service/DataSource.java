package com.people.hub.policyengineapi.service;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;

import java.util.List;
import java.util.Map;

public interface DataSource extends ModuleProvider {

    default String getService() {
        return getModule().getCode();
    }

    // Adjacency Matrix
    // Returns a map. Keys are attributes that the DataSource can fetch. Values are the attributes whose values it requires to fetch the key.
    Map<AttributeIdentifier, List<AttributeIdentifier>> listSupportedAttributes();

    // Fetches the specified attributes, using the values of the required attributes (dependencies)
    Map<AttributeIdentifier, Object> getAttributes(
            List<AttributeIdentifier> attributes,
            Map<AttributeIdentifier, Object> dependencies
    );

    AttributeDefinition getAttributeByIdentifier(String identifier);

    List<AttributeDefinition> getAllAttributeDefinitions();
}
