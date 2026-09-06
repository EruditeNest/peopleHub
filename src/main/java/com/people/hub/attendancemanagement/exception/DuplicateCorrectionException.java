package com.people.hub.attendancemanagement.exception;

/** Thrown when a new correction request is raised for an (employeeId, workDate)
 * that already has a PENDING request — prevents two open requests racing on the same day. */
public class DuplicateCorrectionException extends RuntimeException {
    public DuplicateCorrectionException(String message) {
        super(message);
    }
}
