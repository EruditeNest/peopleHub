package com.people.hub.attendancemanagement.enums;

/** Status of a computed daily work record, used to flag rows that need
 * human attention (HR review / correction request) vs clean automatic computation. */
public enum DailyWorkRecordStatus {
    /** Clean pairing, no anomalies detected. */
    COMPUTED,
    /** Odd number of punches, missing punch-out, or other pairing ambiguity
     * that the algorithm could not resolve confidently. Needs a correction. */
    INCOMPLETE,
    /** No punches at all found for this employee on this day (absence, or
     * device/network failure — cannot distinguish automatically). */
    NO_PUNCHES,
    /** An approved AttendanceCorrection exists and was used instead of raw punches. */
    CORRECTED
}
