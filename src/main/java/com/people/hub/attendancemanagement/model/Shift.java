package com.people.hub.attendancemanagement.model;

import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * Minimal shift definition. This is intentionally lightweight — a full
 * shift-scheduling module is out of scope here, but work-hours calculation
 * is meaningless without at least knowing: (a) where a "day" starts for an
 * employee (so an 11PM-7AM night shift attributes correctly to one day, not
 * split across two), and (b) what counts as the standard daily duration for
 * overtime comparison.
 */
@Entity
@Table(name = "shift")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name; // e.g. "General Shift", "Night Shift"

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // e.g. 09:00, or 23:00 for night shift

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // e.g. 18:00, or 07:00 for night shift (crosses midnight)

    /** True when endTime is numerically before startTime, meaning the shift
     * crosses midnight (e.g. 23:00 -> 07:00). Used to decide the "attendance
     * day" anchor for a punch instead of naive calendar-day grouping. */
    @Column(name = "crosses_midnight", nullable = false)
    private boolean crossesMidnight;

    @Column(name = "standard_minutes", nullable = false)
    private int standardMinutes; // expected work duration, e.g. 480 for 8 hours

    @Column(name = "grace_minutes", nullable = false)
    private int graceMinutes; // late-arrival grace period before marking late

    protected Shift() {
        // JPA
    }

    public Shift(String name, LocalTime startTime, LocalTime endTime,
                 boolean crossesMidnight, int standardMinutes, int graceMinutes) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.crossesMidnight = crossesMidnight;
        this.standardMinutes = standardMinutes;
        this.graceMinutes = graceMinutes;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isCrossesMidnight() { return crossesMidnight; }
    public int getStandardMinutes() { return standardMinutes; }
    public int getGraceMinutes() { return graceMinutes; }
}