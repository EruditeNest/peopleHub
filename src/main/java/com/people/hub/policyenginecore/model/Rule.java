package com.people.hub.policyenginecore.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.people.hub.policyengineapi.enums.DataType;
import com.people.hub.policyenginecore.enums.Operator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    private Integer priority;

    private String attributeIdentifier;

    @Enumerated(EnumType.STRING)
    private Operator operator;

    @Enumerated(EnumType.STRING)
    private DataType constantType;

    private String constantValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "decision", nullable = false)
    private JsonNode decision;

    @Column(nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
