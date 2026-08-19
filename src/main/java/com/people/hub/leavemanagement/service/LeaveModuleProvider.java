package com.people.hub.leavemanagement.service;

import com.people.hub.leavemanagement.enums.LeaveModuleDefinition;
import com.people.hub.policyengineapi.service.ModuleDefinition;
import com.people.hub.policyengineapi.service.ModuleProvider;
import org.springframework.stereotype.Component;

@Component
public class LeaveModuleProvider implements ModuleProvider {

    @Override
    public ModuleDefinition getModule() {
        return LeaveModuleDefinition.INSTANCE;
    }
}
