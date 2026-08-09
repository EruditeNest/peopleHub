package com.people.hub.policyenginecore.model;

import com.people.hub.policyengineapi.service.AttributeDefinition;
import com.people.hub.policyengineapi.service.DecisionType;
import com.people.hub.policyenginecore.enums.Operator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AttributeDefinition attribute;

    @Enumerated(EnumType.STRING)
    private Operator operator;

    @JoinColumn
    private DecisionType decision;
}
