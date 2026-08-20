package com.people.hub.leavemanagement.enums;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;
import com.people.hub.policyengineapi.enums.DataType;
import com.people.hub.policyengineapi.service.AttributeDefinition;

public enum LeaveAttributeDefinition implements AttributeDefinition {
    LEAVE_APPLICATION_ID(
            "leave.application.id",
            DataType.LONG
    );

    private final AttributeIdentifier identifier;
    private final DataType dataType;

    LeaveAttributeDefinition(
            String code,
            DataType dataType
    ) {
        this.identifier = new AttributeIdentifier(code);
        this.dataType = dataType;
    }

    @Override
    public AttributeIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getIdentifierString() {
        return identifier.value();
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }
}
