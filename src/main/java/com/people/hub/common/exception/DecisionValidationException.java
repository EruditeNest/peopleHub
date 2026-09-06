package com.people.hub.common.exception;

import java.util.Arrays;
import java.util.List;

public class DecisionValidationException extends RuntimeException {
    public DecisionValidationException(String message) {
        super(message);
    }

    public DecisionValidationException(String message, String... info) {
        super(message + ": " + Arrays.toString(info));
    }

    public DecisionValidationException(String message, List<String> errors) {
        super(message + ": "+ "\n\t" + errors);
    }

    public DecisionValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
