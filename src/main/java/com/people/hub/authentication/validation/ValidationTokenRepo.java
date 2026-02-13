package com.people.hub.authentication.validation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ValidationTokenRepo extends JpaRepository<ValidationToken, Long> {
    ValidationToken findByUuid(UUID uuid);
}
