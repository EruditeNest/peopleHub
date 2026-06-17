package com.people.hub.attendancemanagement.enums;

/** What kind of fix is being requested. Determines which fields on
 * AttendanceCorrection are expected to be populated. */
public enum CorrectionType {
    /** Employee forgot to punch in/out; requester supplies the missing time(s). */
    MISSING_PUNCH,
    /** A punch was recorded but at the wrong time (e.g. device clock drift,
     * employee punched for a colleague by mistake) — requester supplies corrected time(s). */
    WRONG_TIME,
    /** Full-day override, e.g. approved work-from-home or field duty with no
     * device punches possible; requester supplies a flat worked-minutes value. */
    MANUAL_DAY_OVERRIDE,
    /** Marks the day as an approved leave/holiday, excluding it from absence
     * flags without asserting any worked hours. */
    LEAVE_OR_HOLIDAY
}
