package com.people.hub.core.common.exception;

public class RedisException extends RuntimeException {
    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable e) {
        super(message, e);
    }
}
