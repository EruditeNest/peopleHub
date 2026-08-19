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
}
