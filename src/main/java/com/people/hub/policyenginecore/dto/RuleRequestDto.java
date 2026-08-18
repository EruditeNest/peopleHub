package com.people.hub.policyenginecore.dto;

import com.people.hub.policyengineapi.enums.DataType;
import com.people.hub.policyenginecore.enums.Operator;
import lombok.Data;
import com.fasterxml.jackson.databind.JsonNode;

@Data
public class RuleRequestDto {
    private Long policyId;

    private Integer priority;

    private String attributeIdentifier;

    private Operator operator;

    private DataType constantType;

    private String constantValue;

    private JsonNode decision;

    private boolean active;
}
