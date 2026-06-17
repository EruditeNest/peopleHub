package com.people.hub.attendancemanagement.repository;

import com.people.hub.attendancemanagement.model.EmployeeDeviceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDeviceMappingRepository extends JpaRepository<EmployeeDeviceMapping, Long> {

    Optional<EmployeeDeviceMapping> findByDeviceSerialAndDeviceEmployeePinAndActiveTrue(
            String deviceSerial, String deviceEmployeePin);
}
