package com.hyperlofy.backend.tracking.repository;

import com.hyperlofy.backend.tracking.entity.TrackingEtaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingEtaHistoryRepository extends JpaRepository<TrackingEtaHistory, UUID> {
    List<TrackingEtaHistory> findByTrackingSessionIdOrderByEtaVersionAsc(UUID trackingSessionId);
}
