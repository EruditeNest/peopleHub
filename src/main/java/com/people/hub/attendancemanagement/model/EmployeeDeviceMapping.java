package com.people.hub.attendancemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Maps the device-local enrollment ID ("PIN", typically a small integer the
 * device assigns when a fingerprint is enrolled) to your internal Employee.
 *
 * Why this exists separately rather than just using employee_id as the PIN:
 * devices often impose PIN constraints (numeric only, length limits) and the
 * same employee may be enrolled with different PINs on different devices/branches.
 */
@Entity
@Table(
    name = "employee_device_mapping",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_device_pin",
        columnNames = {"device_serial", "device_employee_pin"}
    )
)
public class EmployeeDeviceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_serial", nullable = false, length = 50)
    private String deviceSerial;

    @Column(name = "device_employee_pin", nullable = false, length = 50)
    private String deviceEmployeePin;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected EmployeeDeviceMapping() {
        // JPA
    }

    public EmployeeDeviceMapping(String deviceSerial, String deviceEmployeePin,
                                  Long employeeId, boolean active, LocalDateTime createdAt) {
        this.deviceSerial = deviceSerial;
        this.deviceEmployeePin = deviceEmployeePin;
        this.employeeId = employeeId;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getDeviceSerial() { return deviceSerial; }
    public String getDeviceEmployeePin() { return deviceEmployeePin; }
    public Long getEmployeeId() { return employeeId; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
