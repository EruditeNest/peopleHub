package com.people.hub.policyengineapi.enums;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public enum DataType {
    STRING,
    INTEGER,
    LONG,
    DECIMAL,
    BOOLEAN,
    DATETIME,
    LIST,
    OBJECT;

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

        if (List.class.isAssignableFrom(type)) {
            return LIST;
        }

        return OBJECT;
    }
}
