package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.ai.logistics.entity.DriverIntelligenceSnapshot;
import com.hyperlofy.backend.ai.logistics.service.DeliveryIntelligenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery/intelligence")
@RequiredArgsConstructor
@Tag(name = "Driver AI Intelligence API", description = "Endpoints for driver performance metrics, reliability scores, and dispatch suitability")
@PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN', 'SUPER_ADMIN')")
public class DriverIntelligenceController {

    private final DeliveryIntelligenceService deliveryIntelligenceService;

    @GetMapping("/snapshot")
    @Operation(summary = "Get Driver Reliability Snapshot", description = "Retrieves driver acceptance rate, completion rate, average speed, and efficiency score.")
    public ResponseEntity<DriverIntelligenceSnapshot> getDriverSnapshot(Principal principal) {
        return ResponseEntity.ok(deliveryIntelligenceService.getDriverIntelligence(UUID.randomUUID()));
    }
}
