package com.people.hub.policyengineapi.service;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.enums.DataType;

public interface AttributeDefinition {

    AttributeIdentifier getIdentifier();

    String getIdentifierString();

    DataType getDataType();
}
