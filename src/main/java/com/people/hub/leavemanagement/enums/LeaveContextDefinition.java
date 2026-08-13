package com.people.hub.leavemanagement.enums;

import com.people.hub.policyengineapi.service.ContextDefinition;

public enum LeaveContextDefinition implements ContextDefinition {
    LEAVE_APPLICATION(
            "leave.application",
            "Leave Application"
    ),

    LEAVE_APPROVAL(
            "leave.approval",
            "Leave Approval"
    );

    private final String code;
    private final String displayName;

    LeaveContextDefinition(String code, String displayName) {
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
