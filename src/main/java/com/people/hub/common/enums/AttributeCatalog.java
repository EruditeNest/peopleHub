package com.people.hub.common.enums;

import com.people.hub.policyengineapi.dto.AttributeIdentifier;

public enum AttributeCatalog {
    LEAVE_APPLICATION_ID("leave.application.id");

    private final AttributeIdentifier identifier;

    AttributeCatalog(String code) {
        this.identifier = new AttributeIdentifier(code);
    }

    public AttributeIdentifier getIdentifier() {
        return identifier;
    }
}
