package com.people.hub.policyenginecore.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.policyengineapi.service.ModuleDefinition;
import com.people.hub.policyengineapi.service.ModuleProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ModuleRegistry {

    private final Map<String, ModuleDefinition> modules;

    public ModuleRegistry(List<ModuleProvider> providers) {
        this.modules = providers.stream()
                .map(ModuleProvider::getModule)
                .collect(Collectors.toMap(
                        ModuleDefinition::getCode,
                        Function.identity()
                ));
    }

    public ModuleDefinition getModuleDefinition(String serviceCode) {
        ModuleDefinition module = modules.get(serviceCode);

        if (module == null) {
            throw new NotFoundException("Module not found", serviceCode);
        }

        return module;
    }
}
