package com.people.hub.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private int status;
    private String error;
    private String path;
    private LocalDateTime timestamp;
}
