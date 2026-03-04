package com.people.hub.core.common.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Long id) {
        super(message + ": " + id);
    }
    public NotFoundException(String message, String info) {
        super(message + ": " + info);
    }
    public NotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
