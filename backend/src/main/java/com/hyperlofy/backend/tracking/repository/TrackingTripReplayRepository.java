package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingTripReplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingTripReplayRepository extends JpaRepository<TrackingTripReplay, UUID> {
    Optional<TrackingTripReplay> findByTrackingSessionId(UUID trackingSessionId);
}
