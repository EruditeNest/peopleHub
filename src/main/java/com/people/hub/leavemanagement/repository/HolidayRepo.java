package com.people.hub.leavemanagement.repository;

import com.people.hub.leavemanagement.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HolidayRepo extends JpaRepository<Holiday, Long> {
}
