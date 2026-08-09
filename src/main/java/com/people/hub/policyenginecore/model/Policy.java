package com.people.hub.policyenginecore.model;

import com.people.hub.policyengineapi.service.ContextDefinition;
import com.people.hub.policyengineapi.service.DecisionType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String policyCode; // Naming convention Namespace.Name Example: leave.approval

    private String name;

    private ContextDefinition context;

    private DecisionType decisionType;
}
