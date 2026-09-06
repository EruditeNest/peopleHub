package com.people.hub.attendancemanagement.enums;

/** How the employee was verified at the device. Useful for fallback auditing
 * since fingerprint match can fail and devices fall back to card/password. */
public enum VerifyMode {
    FINGERPRINT,
    FACE,
    CARD,
    PASSWORD,
    UNKNOWN
}
