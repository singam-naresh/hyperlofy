package com.hyperlofy.backend.tracking.controller;

import com.hyperlofy.backend.tracking.entity.TrackingLocation;
import com.hyperlofy.backend.tracking.entity.TrackingSession;
import com.hyperlofy.backend.tracking.service.LiveTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking/driver")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Engine Driver API", description = "Endpoints for delivery partners to stream GPS updates and update order trip progress")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class TrackingDriverController {

    private final LiveTrackingService trackingService;

    @PostMapping("/start")
    @Operation(summary = "Start Live Tracking Session", description = "Initializes GPS streaming for assigned order delivery.")
    public ResponseEntity<TrackingSession> startTracking(@RequestParam UUID orderId, @RequestParam UUID driverId) {
        return ResponseEntity.ok(trackingService.startTrackingSession(orderId, driverId));
    }

    @PostMapping("/location")
    @Operation(summary = "Record Real-Time Location Update", description = "Streams driver GPS coordinates, speed, heading, and device timestamp.")
    public ResponseEntity<TrackingLocation> streamLocation(
            @RequestParam UUID orderId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Double heading,
            @RequestParam(required = false) Double speedKmh,
            @RequestParam(required = false) Double accuracyMeters) {
        return ResponseEntity.ok(trackingService.recordLocationUpdate(orderId, lat, lng, heading, speedKmh, accuracyMeters));
    }

    @PostMapping("/status")
    @Operation(summary = "Update Tracking Status", description = "Updates order progress lifecycle state (e.g. ARRIVED_AT_PICKUP, PICKUP_COMPLETED, IN_TRANSIT, DELIVERED).")
    public ResponseEntity<TrackingSession> updateStatus(@RequestParam UUID orderId, @RequestParam String status) {
        return ResponseEntity.ok(trackingService.updateTrackingStatus(orderId, status));
    }
}
