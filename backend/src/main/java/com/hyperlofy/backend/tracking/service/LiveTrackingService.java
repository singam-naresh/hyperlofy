package com.hyperlofy.backend.tracking.service;

import com.hyperlofy.backend.tracking.entity.TrackingEta;
import com.hyperlofy.backend.tracking.entity.TrackingLocation;
import com.hyperlofy.backend.tracking.entity.TrackingSession;
import com.hyperlofy.backend.tracking.entity.TrackingTimeline;
import com.hyperlofy.backend.tracking.repository.TrackingEtaRepository;
import com.hyperlofy.backend.tracking.repository.TrackingLocationRepository;
import com.hyperlofy.backend.tracking.repository.TrackingSessionRepository;
import com.hyperlofy.backend.tracking.repository.TrackingTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveTrackingService {

    private static final Logger log = LoggerFactory.getLogger(LiveTrackingService.class);

    private final TrackingSessionRepository sessionRepository;
    private final TrackingLocationRepository locationRepository;
    private final TrackingEtaRepository etaRepository;
    private final TrackingTimelineRepository timelineRepository;

    @Transactional
    public TrackingSession startTrackingSession(UUID orderId, UUID driverId) {
        log.info("[LIVE TRACKING ENGINE] Starting tracking session OrderId={}, DriverId={}", orderId, driverId);

        TrackingSession session = sessionRepository.findByOrderId(orderId).orElseGet(() ->
                TrackingSession.builder()
                        .orderId(orderId)
                        .driverId(driverId)
                        .status("DRIVER_EN_ROUTE")
                        .startedAt(ZonedDateTime.now())
                        .build()
        );

        TrackingSession saved = sessionRepository.save(session);
        recordTimelineEvent(saved.getId(), "TRACKING_STARTED", "Driver started en-route navigation to pickup location");
        return saved;
    }

    @Transactional
    public TrackingLocation recordLocationUpdate(UUID orderId, Double lat, Double lng, Double heading, Double speedKmh, Double accuracyMeters) {
        // Location validation rule: speed > 150 km/h is rejected as GPS anomaly
        if (speedKmh != null && speedKmh > 150.0) {
            log.warn("[LIVE TRACKING ENGINE] Rejected anomalous GPS update: speed {} km/h exceeds 150 km/h threshold", speedKmh);
            throw new IllegalArgumentException("Invalid GPS update: Speed exceeds maximum physical threshold");
        }

        TrackingSession session = sessionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking session not found for order: " + orderId));

        TrackingLocation loc = TrackingLocation.builder()
                .trackingSessionId(session.getId())
                .latitude(lat)
                .longitude(lng)
                .heading(heading != null ? heading : 0.0)
                .speedKmh(speedKmh != null ? speedKmh : 0.0)
                .accuracyMeters(accuracyMeters != null ? accuracyMeters : 5.0)
                .deviceTimestamp(ZonedDateTime.now())
                .build();

        TrackingLocation saved = locationRepository.save(loc);
        recalculateEta(session.getId(), 4.2); // Recalculate ETA for remaining distance 4.2km
        return saved;
    }

    @Transactional
    public TrackingEta recalculateEta(UUID trackingSessionId, Double remainingDistanceKm) {
        double avgSpeed = 25.0; // Default average urban speed in km/h
        int remainingDurationMinutes = (int) Math.ceil((remainingDistanceKm / avgSpeed) * 60);
        ZonedDateTime calculatedEta = ZonedDateTime.now().plusMinutes(remainingDurationMinutes);

        TrackingEta eta = TrackingEta.builder()
                .trackingSessionId(trackingSessionId)
                .remainingDistanceKm(remainingDistanceKm)
                .remainingDurationMinutes(remainingDurationMinutes)
                .calculatedEta(calculatedEta)
                .averageSpeedKmh(avgSpeed)
                .build();

        return etaRepository.save(eta);
    }

    @Transactional
    public TrackingSession updateTrackingStatus(UUID orderId, String newStatus) {
        TrackingSession session = sessionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking session not found for order: " + orderId));

        session.setStatus(newStatus);
        if ("DELIVERED".equals(newStatus) || "TRACKING_COMPLETED".equals(newStatus)) {
            session.setCompletedAt(ZonedDateTime.now());
        }

        TrackingSession saved = sessionRepository.save(session);
        recordTimelineEvent(saved.getId(), newStatus, "Tracking session status updated to " + newStatus);
        return saved;
    }

    @Transactional
    public void recordTimelineEvent(UUID trackingSessionId, String eventName, String description) {
        TrackingTimeline event = TrackingTimeline.builder()
                .trackingSessionId(trackingSessionId)
                .eventName(eventName)
                .description(description)
                .eventTime(ZonedDateTime.now())
                .build();
        timelineRepository.save(event);
    }

    @Transactional(readOnly = true)
    public TrackingSession getTrackingSession(UUID orderId) {
        return sessionRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Tracking session not found for order: " + orderId));
    }

    @Transactional(readOnly = true)
    public TrackingLocation getLatestLocation(UUID orderId) {
        TrackingSession session = getTrackingSession(orderId);
        return locationRepository.findFirstByTrackingSessionIdOrderByDeviceTimestampDesc(session.getId())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TrackingTimeline> getTimeline(UUID orderId) {
        TrackingSession session = getTrackingSession(orderId);
        return timelineRepository.findByTrackingSessionIdOrderByEventTimeAsc(session.getId());
    }

    @Transactional(readOnly = true)
    public TrackingEta getLatestEta(UUID orderId) {
        TrackingSession session = getTrackingSession(orderId);
        return etaRepository.findFirstByTrackingSessionIdOrderByCreatedAtDesc(session.getId())
                .orElse(null);
    }
}
