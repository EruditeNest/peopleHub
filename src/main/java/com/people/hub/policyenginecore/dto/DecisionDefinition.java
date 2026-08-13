package com.people.hub.policyenginecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionDefinition {
    private String code;
    private String displayName;
    private List<DecisionFieldDefinition> fields;
}
