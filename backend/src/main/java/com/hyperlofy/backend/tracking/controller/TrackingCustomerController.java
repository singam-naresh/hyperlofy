package com.hyperlofy.backend.tracking.controller;

import com.hyperlofy.backend.tracking.entity.TrackingEta;
import com.hyperlofy.backend.tracking.entity.TrackingLocation;
import com.hyperlofy.backend.tracking.entity.TrackingSession;
import com.hyperlofy.backend.tracking.entity.TrackingTimeline;
import com.hyperlofy.backend.tracking.service.LiveTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking/customer")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Engine Customer API", description = "Endpoints for customers to track driver location, view trip timeline, and inspect real-time ETA")
@PreAuthorize("hasRole('CUSTOMER')")
public class TrackingCustomerController {

    private final LiveTrackingService trackingService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Customer Tracking View", description = "Returns active tracking session state for customer's order.")
    public ResponseEntity<TrackingSession> getSession(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getTrackingSession(orderId));
    }

    @GetMapping("/{orderId}/location")
    @Operation(summary = "Get Latest Driver Location", description = "Returns latest GPS coordinates of delivery partner for live map rendering.")
    public ResponseEntity<TrackingLocation> getLatestLocation(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getLatestLocation(orderId));
    }

    @GetMapping("/{orderId}/eta")
    @Operation(summary = "Get Real-Time ETA", description = "Returns calculated estimated arrival time and remaining trip duration.")
    public ResponseEntity<TrackingEta> getEta(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getLatestEta(orderId));
    }

    @GetMapping("/{orderId}/timeline")
    @Operation(summary = "Get Order Tracking Timeline", description = "Returns chronological list of order milestone events.")
    public ResponseEntity<List<TrackingTimeline>> getTimeline(@PathVariable UUID orderId) {
        return ResponseEntity.ok(trackingService.getTimeline(orderId));
    }
}
