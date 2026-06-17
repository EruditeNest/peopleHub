package com.people.hub.attendancemanagement.model;

import com.people.hub.attendancemanagement.enums.PunchDirection;
import com.people.hub.attendancemanagement.enums.VerifyMode;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable raw punch event as received from a biometric device.
 *
 * IMPORTANT: Rows in this table are NEVER updated or deleted once inserted.
 * This is the source-of-truth audit log. Corrections are modeled as a
 * separate entity (AttendanceCorrection) that overrides computed work hours,
 * never this raw log.
 */
@Entity
@Table(
    name = "attendance_punch",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_device_punch",
        columnNames = {"device_serial", "device_employee_pin", "punch_timestamp", "device_sequence"}
    ),
    indexes = {
        @Index(name = "idx_punch_employee_time", columnList = "employee_id, punch_timestamp"),
        @Index(name = "idx_punch_device_serial", columnList = "device_serial")
    }
)
public class AttendancePunch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Serial number of the physical device that generated this punch. */
    @Column(name = "device_serial", nullable = false, length = 50)
    private String deviceSerial;

    /**
     * The raw employee identifier as known to the device (its "PIN").
     * This is NOT necessarily your internal employee ID — it must be resolved
     * via EmployeeDeviceMapping. Stored as-is for audit/debugging even if
     * resolution fails.
     */
    @Column(name = "device_employee_pin", nullable = false, length = 50)
    private String deviceEmployeePin;

    /**
     * Resolved internal employee ID, nullable because resolution can fail
     * (unmapped device PIN) and we still want to store the raw punch rather
     * than drop it.
     */
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "punch_timestamp", nullable = false)
    private LocalDateTime punchTimestamp;

    /**
     * Sequence/counter from the device payload line, used as a uniqueness
     * tiebreaker for devices that don't guarantee unique timestamps
     * (e.g. two punches in the same second) or replay the same line twice.
     */
    @Column(name = "device_sequence", nullable = false)
    private Integer deviceSequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 20)
    private PunchDirection direction; // IN, OUT, or UNKNOWN if device doesn't report it

    @Enumerated(EnumType.STRING)
    @Column(name = "verify_mode", length = 20)
    private VerifyMode verifyMode; // FINGERPRINT, FACE, CARD, PASSWORD, UNKNOWN

    /** Raw status/workcode fields from the device, kept for debugging/audit. */
    @Column(name = "raw_status_code", length = 10)
    private String rawStatusCode;

    @Column(name = "raw_payload_line", length = 500)
    private String rawPayloadLine;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved; // false if device PIN couldn't be mapped to an employee

    protected AttendancePunch() {
        // JPA
    }

    public AttendancePunch(String deviceSerial, String deviceEmployeePin, Long employeeId,
                            LocalDateTime punchTimestamp, Integer deviceSequence,
                            PunchDirection direction, VerifyMode verifyMode,
                            String rawStatusCode, String rawPayloadLine,
                            LocalDateTime receivedAt, boolean resolved) {
        this.deviceSerial = deviceSerial;
        this.deviceEmployeePin = deviceEmployeePin;
        this.employeeId = employeeId;
        this.punchTimestamp = punchTimestamp;
        this.deviceSequence = deviceSequence;
        this.direction = direction;
        this.verifyMode = verifyMode;
        this.rawStatusCode = rawStatusCode;
        this.rawPayloadLine = rawPayloadLine;
        this.receivedAt = receivedAt;
        this.resolved = resolved;
    }

    // Getters only — no setters. This entity is write-once.

    public Long getId() { return id; }
    public String getDeviceSerial() { return deviceSerial; }
    public String getDeviceEmployeePin() { return deviceEmployeePin; }
    public Long getEmployeeId() { return employeeId; }
    public LocalDateTime getPunchTimestamp() { return punchTimestamp; }
    public Integer getDeviceSequence() { return deviceSequence; }
    public PunchDirection getDirection() { return direction; }
    public VerifyMode getVerifyMode() { return verifyMode; }
    public String getRawStatusCode() { return rawStatusCode; }
    public String getRawPayloadLine() { return rawPayloadLine; }
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public boolean isResolved() { return resolved; }
}
