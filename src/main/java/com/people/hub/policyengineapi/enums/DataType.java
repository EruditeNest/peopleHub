package com.people.hub.policyengineapi.enums;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public enum DataType {
    STRING,
    INTEGER,
    LONG,
    DECIMAL,
    BOOLEAN,
    DATETIME,
    ENUM;

    public static DataType fromJavaType(Class<?> type) {
        if (type == String.class) {
            return STRING;
        }
        if (type == Integer.class || type == int.class) {
            return INTEGER;
        }
        if (type == Long.class || type == long.class) {
            return LONG;
        }
        if (type == BigDecimal.class) {
            return DECIMAL;
        }
        if (type == Boolean.class || type == boolean.class) {
            return BOOLEAN;
        }
        if (type == LocalDateTime.class) {
            return DATETIME;
        }
        if (type.isEnum()) {
            return ENUM;
        }
        throw new IllegalArgumentException("Unsupported decision field type: " + type.getName());
    }

    public static Class<?> fromDataType(DataType type) {
        if (type.equals(STRING)) {
            return String.class;
        }
        if (type.equals(INTEGER)) {
            return Integer.class;
        }
        if (type.equals(LONG)) {
            return Long.class;
        }
        if (type.equals(DECIMAL)) {
            return BigDecimal.class;
        }
        if (type.equals(BOOLEAN)) {
            return Boolean.class;
        }
        if (type.equals(DATETIME)) {
            return LocalDateTime.class;
        }
        if (type.equals(ENUM)) {
            return Enum.class;
        }
        throw new IllegalArgumentException("Unsupported decision field type: " + type);
    }
}
