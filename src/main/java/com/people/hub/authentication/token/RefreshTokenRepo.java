package com.people.hub.authentication.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUserId(Long userId);
    Optional<RefreshToken> findByToken(UUID token);

    @Modifying
    @Query("""
        UPDATE RefreshToken r SET r.revoked = true
        WHERE r.userId = :userId AND r.revoked = false
    """)
    int revokeActiveTokensByUserId(@Param("userId") Long userId);
}
