package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingEta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingEtaRepository extends JpaRepository<TrackingEta, UUID> {
    Optional<TrackingEta> findFirstByTrackingSessionIdOrderByCreatedAtDesc(UUID trackingSessionId);
}
