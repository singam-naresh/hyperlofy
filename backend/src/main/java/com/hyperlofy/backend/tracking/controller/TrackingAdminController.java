package com.hyperlofy.backend.tracking.controller;

import com.hyperlofy.backend.tracking.entity.TrackingEta;
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
@RequestMapping("/api/v1/tracking/admin")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Engine Admin API", description = "Endpoints for platform operations to oversee all active tracking sessions and trip metrics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class TrackingAdminController {

    private final LiveTrackingService trackingService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Admin Inspect Tracking Session", description = "Returns full tracking session details for operational oversight.")
    public ResponseEntity<TrackingSession> getSession(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getTrackingSession(orderId));
    }

    @GetMapping("/{orderId}/location")
    @Operation(summary = "Admin Get Driver Location", description = "Returns real-time GPS coordinates for active order dispatch monitoring.")
    public ResponseEntity<TrackingLocation> getDriverLocation(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getLatestLocation(orderId));
    }

    @GetMapping("/{orderId}/eta")
    @Operation(summary = "Admin Get ETA", description = "Inspects system ETA prediction and remaining distance.")
    public ResponseEntity<TrackingEta> getEta(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getLatestEta(orderId));
    }
}
