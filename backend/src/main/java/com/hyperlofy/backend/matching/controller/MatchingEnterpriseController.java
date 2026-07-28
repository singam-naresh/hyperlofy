package com.hyperlofy.backend.matching.controller;

import com.hyperlofy.backend.matching.entity.MatchingFairness;
import com.hyperlofy.backend.matching.entity.MatchingReservation;
import com.hyperlofy.backend.matching.entity.MatchingSurgeZone;
import com.hyperlofy.backend.matching.service.MatchingEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matching/enterprise")
@RequiredArgsConstructor
@Tag(name = "Matching Engine Enterprise Addendum API", description = "Endpoints for scheduled driver reservations, surge zone demand multipliers, and fleet fairness metrics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MatchingEnterpriseController {

    private final MatchingEnterpriseService enterpriseService;

    @PostMapping("/reservations")
    @Operation(summary = "Reserve Driver for Scheduled Order", description = "Pre-assigns driver for a scheduled order dispatch window with reservation expiry.")
    public ResponseEntity<MatchingReservation> reserveDriver(
            @RequestParam UUID orderId,
            @RequestParam UUID driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime scheduledTime) {
        return ResponseEntity.ok(enterpriseService.reserveDriverForScheduledOrder(orderId, driverId, scheduledTime));
    }

    @PostMapping("/surge")
    @Operation(summary = "Update Surge Zone Multiplier", description = "Recalculates dynamic surge multiplier based on real-time demand vs supply levels in zone.")
    public ResponseEntity<MatchingSurgeZone> updateSurgeZone(@RequestParam String zoneName, @RequestParam Double demand, @RequestParam Double supply) {
        return ResponseEntity.ok(enterpriseService.updateSurgeZoneDemand(zoneName, demand, supply));
    }

    @GetMapping("/fairness/{driverId}")
    @Operation(summary = "Get Driver Fairness Score", description = "Retrieves workload distribution, idle time, and bias avoidance metrics for a delivery partner.")
    public ResponseEntity<MatchingFairness> getFairnessScore(@PathVariable UUID driverId) {
        return ResponseEntity.ok(enterpriseService.getDriverFairnessScore(driverId));
    }
}
