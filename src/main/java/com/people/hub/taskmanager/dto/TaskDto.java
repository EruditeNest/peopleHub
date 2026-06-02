package com.people.hub.taskmanager.dto;

import com.people.hub.taskmanager.enums.PriorityEnum;
import com.people.hub.taskmanager.enums.StatusEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskDto {
    private String name;
    private String description;
    private Long projectId;
    private Long assignedTo;
    private Long assignedBy;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    private PriorityEnum priority;

    private LocalDate dueDate;
    private int requiredManDays;
}
