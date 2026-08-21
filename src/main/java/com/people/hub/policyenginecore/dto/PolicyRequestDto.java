package com.people.hub.policyenginecore.dto;

import com.people.hub.policyenginecore.enums.RuleCombinationStrategy;
import lombok.Data;

@Data
public class PolicyRequestDto {
    private String name;

    private String serviceCode;

    private String contextCode;

    private String decisionCode;

    private RuleCombinationStrategy ruleCombinationStrategy;

    private boolean active;
}
