package com.people.hub.attendancemanagement.enums;

/**
 * Many ADMS devices don't reliably report check-in vs check-out — some leave
 * it to the application to infer by alternating per employee per day.
 * UNKNOWN is a legitimate, common value; don't assume it'll always be set.
 */
public enum PunchDirection {
    IN,
    OUT,
    UNKNOWN
}
