package com.people.hub.attendancemanagement.exception;

/** Thrown when a push arrives from a device serial not in our registry, or
 * registered but marked inactive. Caught by the controller to decide HTTP response. */
public class UnknownDeviceException extends RuntimeException {
    public UnknownDeviceException(String message) {
        super(message);
    }
}
