package com.people.hub.taskmanager.dto;

import com.people.hub.taskmanager.enums.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private String name;
    private String projectCode;
    private String description;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Long managerId;
    private Long clientId;
    private LocalDate startDate;
    private LocalDate endDate;
}
