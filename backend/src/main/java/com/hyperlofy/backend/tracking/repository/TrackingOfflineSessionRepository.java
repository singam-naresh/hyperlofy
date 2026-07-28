package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingOfflineSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingOfflineSessionRepository extends JpaRepository<TrackingOfflineSession, UUID> {
    Optional<TrackingOfflineSession> findByTrackingSessionId(UUID trackingSessionId);
}
