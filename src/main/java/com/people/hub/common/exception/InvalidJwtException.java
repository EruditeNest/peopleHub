package com.people.hub.common.exception;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException(String message) {
        super(message);
    }
    public InvalidJwtException(String message, Throwable e) {
        super(message, e);
    }
}
