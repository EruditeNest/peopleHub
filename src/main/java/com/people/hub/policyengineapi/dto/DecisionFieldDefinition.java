package com.people.hub.policyengineapi.dto;

import com.people.hub.policyengineapi.enums.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionFieldDefinition {
    private String name;
    private DataType dataType;
    private boolean required;
    private String displayName;
    private String description;
}
