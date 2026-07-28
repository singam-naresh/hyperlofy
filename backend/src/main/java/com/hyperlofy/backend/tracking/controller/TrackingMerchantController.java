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
@RequestMapping("/api/v1/tracking/merchant")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Engine Merchant API", description = "Endpoints for merchants to monitor driver arrival at pickup store")
@PreAuthorize("hasRole('MERCHANT')")
public class TrackingMerchantController {

    private final LiveTrackingService trackingService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Merchant Tracking View", description = "Monitors driver arrival progress for store order pickup.")
    public ResponseEntity<TrackingSession> getSession(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getTrackingSession(orderId));
    }

    @GetMapping("/{orderId}/location")
    @Operation(summary = "Get Driver Pickup Location", description = "Inspects approaching delivery partner GPS coordinates.")
    public ResponseEntity<TrackingLocation> getDriverLocation(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getLatestLocation(orderId));
    }
}
