package com.people.hub.attendancemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Which shift an employee is assigned to, effective from a given date.
 * Kept date-ranged (not just a static FK on Employee) because shift
 * reassignment is common (rotating shifts, temporary night-shift coverage)
 * and historical work-hours recalculation needs to know what shift applied
 * on a PAST date, not just the employee's current shift.
 */
@Entity
@Table(name = "employee_shift_assignment",
        indexes = @Index(name = "idx_emp_shift_effective", columnList = "employee_id, effective_from"))
public class EmployeeShiftAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "shift_id", nullable = false)
    private Long shiftId;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    /** Null means "still in effect". */
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    protected EmployeeShiftAssignment() {
        // JPA
    }

    public EmployeeShiftAssignment(Long employeeId, Long shiftId, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.employeeId = employeeId;
        this.shiftId = shiftId;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public Long getShiftId() { return shiftId; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
}
