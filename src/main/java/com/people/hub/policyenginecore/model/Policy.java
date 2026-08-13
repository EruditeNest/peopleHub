package com.people.hub.policyenginecore.model;

import com.people.hub.policyenginecore.enums.RuleCombinationStrategy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Naming convention Namespace.Name Example: leave.approval

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contextCode;

    @Column(nullable = false)
    private String decisionCode;

    @Enumerated(EnumType.STRING)
    private RuleCombinationStrategy ruleCombinationStrategy;

    @Column(nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
