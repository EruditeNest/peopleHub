package com.people.hub.leavemanagement.dto.decision;

import com.people.hub.leavemanagement.enums.LeaveStatus;
import com.people.hub.policyengineapi.annotation.DecisionField;
import com.people.hub.policyengineapi.service.Decision;
import lombok.Data;

@Data
public class LeaveApplicationDecision implements Decision {

    @DecisionField(required = true, displayName = "APPROVAL")
    private LeaveStatus approval;

    @DecisionField(required = false)
    private String reason;

    @Override
    public String getCode() {
        return "leave.application.decision";
    }

    @Override
    public String getDisplayName() {
        return "LEAVE APPLICATION DECISION";
    }
}
