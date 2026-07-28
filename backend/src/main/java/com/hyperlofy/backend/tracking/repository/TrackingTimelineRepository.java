package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingTimelineRepository extends JpaRepository<TrackingTimeline, UUID> {
    List<TrackingTimeline> findByTrackingSessionIdOrderByEventTimeAsc(UUID trackingSessionId);
}
