package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingSessionRepository extends JpaRepository<TrackingSession, UUID> {
    Optional<TrackingSession> findByOrderId(UUID orderId);
}
