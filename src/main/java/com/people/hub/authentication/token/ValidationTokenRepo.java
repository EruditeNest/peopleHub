package com.people.hub.authentication.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ValidationTokenRepo extends JpaRepository<ValidationToken, Long> {
    ValidationToken findByUuid(UUID uuid);
}
