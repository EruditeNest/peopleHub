package com.people.hub.attendancemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registry of physical devices authorized to push attendance data.
 * Any push from a serial number NOT in this table is rejected — this is
 * your primary defense against a spoofed device hitting your public endpoint.
 */
@Entity
@Table(name = "biometric_device")
public class BiometricDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;

    @Column(name = "label", nullable = false, length = 100)
    private String label; // e.g. "Main Gate - Floor 1"

    @Column(name = "office_location_id", nullable = false)
    private Long officeLocationId;

    @Column(name = "active", nullable = false)
    private boolean active;

    /** Shared secret the device can optionally send (some firmwares support a
     * comm key / CommPwd). Not all devices support this, but use it if yours does. */
    @Column(name = "comm_key", length = 50)
    private String commKey;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    protected BiometricDevice() {
        // JPA
    }

    public BiometricDevice(String serialNumber, String label, Long officeLocationId,
                            boolean active, String commKey, LocalDateTime registeredAt) {
        this.serialNumber = serialNumber;
        this.label = label;
        this.officeLocationId = officeLocationId;
        this.active = active;
        this.commKey = commKey;
        this.registeredAt = registeredAt;
    }

    public void markSeenNow(LocalDateTime now) {
        this.lastSeenAt = now;
    }

    public Long getId() { return id; }
    public String getSerialNumber() { return serialNumber; }
    public String getLabel() { return label; }
    public Long getOfficeLocationId() { return officeLocationId; }
    public boolean isActive() { return active; }
    public String getCommKey() { return commKey; }
    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
