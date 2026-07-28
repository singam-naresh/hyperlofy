package com.hyperlofy.backend.tracking.controller;

import com.hyperlofy.backend.tracking.entity.TrackingEtaHistory;
import com.hyperlofy.backend.tracking.entity.TrackingOfflineSession;
import com.hyperlofy.backend.tracking.entity.TrackingTripReplay;
import com.hyperlofy.backend.tracking.service.LiveTrackingEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking/enterprise")
@RequiredArgsConstructor
@Tag(name = "Live Tracking Engine Enterprise Addendum API", description = "Endpoints for predictive ETA version history, trip replay playback, and offline GPS buffer sync")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class LiveTrackingEnterpriseController {

    private final LiveTrackingEnterpriseService enterpriseService;

    @GetMapping("/sessions/{sessionId}/eta-history")
    @Operation(summary = "Get Predictive ETA Version History", description = "Returns complete audit history of ETA recalculations and confidence scores.")
    public ResponseEntity<List<TrackingEtaHistory>> getEtaHistory(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(enterpriseService.getEtaHistory(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/replay")
    @Operation(summary = "Generate Trip Replay Playback", description = "Assembles trip replay JSON payload including speed, heading, stops, and idle duration timelines.")
    public ResponseEntity<TrackingTripReplay> generateReplay(@PathVariable UUID sessionId, @RequestBody String replayJson) {
        return ResponseEntity.ok(enterpriseService.generateTripReplay(sessionId, replayJson));
    }

    @PostMapping("/sessions/{sessionId}/offline-sync")
    @Operation(summary = "Synchronize Offline GPS Buffer", description = "Flushes offline location buffer upon network reconnection with duplicate elimination.")
    public ResponseEntity<TrackingOfflineSession> syncOfflineBuffer(@PathVariable UUID sessionId, @RequestParam Integer pointsCount) {
        return ResponseEntity.ok(enterpriseService.syncOfflineBuffer(sessionId, pointsCount));
    }
}
