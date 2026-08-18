package com.people.hub.leavemanagement.enums;

import com.people.hub.policyengineapi.service.ModuleDefinition;

public enum LeaveModuleDefinition implements ModuleDefinition {

    INSTANCE("leave", "Leave");

    private final String code;
    private final String displayName;

    LeaveModuleDefinition(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
