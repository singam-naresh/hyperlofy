package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, UUID> {
    Optional<DeviceSession> findByRefreshToken(String token);
    List<DeviceSession> findByUserIdAndRevokedFalse(UUID userId);
}
