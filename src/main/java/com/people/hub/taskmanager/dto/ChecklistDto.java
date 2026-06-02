package com.people.hub.taskmanager.dto;

import lombok.Data;

@Data
public class ChecklistDto {
    private String name;
    private String description;
    private Long taskId;
}
