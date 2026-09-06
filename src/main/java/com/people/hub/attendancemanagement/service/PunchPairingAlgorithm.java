package com.people.hub.attendancemanagement.service;

import com.people.hub.attendancemanagement.dto.PunchPairingResult;
import com.people.hub.attendancemanagement.model.AttendancePunch;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pairs a chronologically-ordered list of punches into IN/OUT durations and
 * sums them. Isolated into its own class (no Spring DI dependencies beyond
 * @Component, no repository access) specifically so it's trivial to unit
 * test with hand-built punch lists covering every edge case below.
 *
 * Pairing strategy: ALTERNATION, not relying on the device-reported
 * direction field. Reasoning: direction codes are inconsistently populated
 * across firmware/vendors (see AdmsAttLogParser notes), but punches are
 * reliably timestamp-ordered. So: sort by time, treat punch 1 as IN, punch 2
 * as OUT, punch 3 as IN, punch 4 as OUT, etc. This naturally handles lunch
 * breaks (IN, OUT-for-lunch, IN-from-lunch, OUT) as two separate pairs that
 * both get summed into total worked minutes.
 *
 * Edge cases handled explicitly:
 *  - Zero punches: NO_PUNCHES is the caller's concern (this class isn't
 *    invoked at all in that case); not handled here.
 *  - Exactly one punch: cannot pair -> result is NOT clean, zero minutes.
 *  - Odd punch count overall (e.g. 3, 5): the trailing unpaired punch is
 *    ignored for duration purposes but the result is marked NOT clean so
 *    the caller flags the day INCOMPLETE for human review.
 *  - Duplicate/near-duplicate timestamps (e.g. double-tap on the scanner):
 *    a pair with near-zero duration (under MIN_PAIR_MINUTES) is dropped
 *    entirely rather than counted, and does NOT consume a "pair slot".
 */
@Component
public class PunchPairingAlgorithm {

    /** Pairs shorter than this are treated as scanner double-taps, not a
     * real in/out cycle, and are excluded from both duration and pair count. */
    private static final int MIN_PAIR_MINUTES = 1;

    public PunchPairingResult pair(List<AttendancePunch> chronologicalPunches) {
        if (chronologicalPunches.isEmpty()) {
            return new PunchPairingResult(null, null, 0, 0, false);
        }

        List<LocalDateTime> timestamps = chronologicalPunches.stream()
                .map(AttendancePunch::getPunchTimestamp)
                .sorted()
                .toList();

        LocalDateTime firstIn = timestamps.get(0);
        LocalDateTime lastOut = timestamps.get(timestamps.size() - 1);

        if (timestamps.size() == 1) {
            // A single isolated punch can't be paired into any duration at all.
            return new PunchPairingResult(firstIn, null, 0, 0, false);
        }

        int totalMinutes = 0;
        int pairCount = 0;

        int i = 0;
        while (i + 1 < timestamps.size()) {
            LocalDateTime in = timestamps.get(i);
            LocalDateTime out = timestamps.get(i + 1);
            long minutes = Duration.between(in, out).toMinutes();

            if (minutes < MIN_PAIR_MINUTES) {
                // Degenerate pair (double-tap). Skip both timestamps without
                // counting a pair or duration, then continue from i+2.
                i += 2;
                continue;
            }

            totalMinutes += (int) minutes;
            pairCount++;
            i += 2;
        }

        // Odd total count => last timestamp never had a partner => ambiguous day.
        boolean clean = (timestamps.size() % 2 == 0);

        return new PunchPairingResult(firstIn, lastOut, totalMinutes, pairCount, clean);
    }
}