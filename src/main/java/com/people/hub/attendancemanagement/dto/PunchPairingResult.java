package com.people.hub.attendancemanagement.dto;

import java.time.LocalDateTime;

/** Internal result of running the IN/OUT pairing algorithm over a day's
 * punches. Not persisted directly — its fields get folded into DailyWorkRecord. */
public class PunchPairingResult {

    private final LocalDateTime firstIn;
    private final LocalDateTime lastOut;
    private final int totalWorkedMinutes;
    private final int pairCount;
    private final boolean clean; // false if pairing was ambiguous (odd punch count etc.)

    public PunchPairingResult(LocalDateTime firstIn, LocalDateTime lastOut,
                              int totalWorkedMinutes, int pairCount, boolean clean) {
        this.firstIn = firstIn;
        this.lastOut = lastOut;
        this.totalWorkedMinutes = totalWorkedMinutes;
        this.pairCount = pairCount;
        this.clean = clean;
    }

    public LocalDateTime getFirstIn() { return firstIn; }
    public LocalDateTime getLastOut() { return lastOut; }
    public int getTotalWorkedMinutes() { return totalWorkedMinutes; }
    public int getPairCount() { return pairCount; }
    public boolean isClean() { return clean; }
}