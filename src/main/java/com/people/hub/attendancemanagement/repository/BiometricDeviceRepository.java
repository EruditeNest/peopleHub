package com.people.hub.attendancemanagement.repository;

import com.company.attendance.entity.BiometricDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BiometricDeviceRepository extends JpaRepository<BiometricDevice, Long> {

    Optional<BiometricDevice> findBySerialNumber(String serialNumber);

    Optional<BiometricDevice> findBySerialNumberAndActiveTrue(String serialNumber);
}
