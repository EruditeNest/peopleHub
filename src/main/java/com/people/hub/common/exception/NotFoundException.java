package com.people.hub.common.exception;

import java.util.Arrays;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException(String message, Long... id) {
        super(message + ": " + Arrays.toString(id));
    }
    public NotFoundException(String message, String... info) {
        super(message + ": " + Arrays.toString(info));
    }
    public NotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
