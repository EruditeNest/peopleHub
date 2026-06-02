package com.people.hub.taskmanager.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class TimeLogDto {
    private String description;
    private Long taskId;
    private Long userId;
    private LocalDate workDate;
    private Instant startTime;
    private Instant endTime;
}
