package com.people.hub.attendancemanagement.enums;

/** State machine for an attendance correction request. Legal transitions:
 * PENDING -> APPROVED, PENDING -> REJECTED, PENDING -> WITHDRAWN.
 * No transitions out of APPROVED/REJECTED/WITHDRAWN — if a mistake is found
 * post-approval, raise a NEW correction request rather than mutating this one,
 * to preserve a clean audit trail of who decided what and when. */
public enum CorrectionStatus {
    PENDING,
    APPROVED,
    REJECTED,
    WITHDRAWN
}
