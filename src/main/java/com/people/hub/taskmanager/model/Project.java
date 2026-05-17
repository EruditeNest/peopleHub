package com.people.hub.taskmanager.model;

import com.people.hub.taskmanager.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String projectCode;
    private String description;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Long managerId;
    private Long clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isDeleted;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
