package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackingLocationRepository extends JpaRepository<TrackingLocation, UUID> {
    List<TrackingLocation> findByTrackingSessionIdOrderByDeviceTimestampDesc(UUID trackingSessionId);
    Optional<TrackingLocation> findFirstByTrackingSessionIdOrderByDeviceTimestampDesc(UUID trackingSessionId);
}
