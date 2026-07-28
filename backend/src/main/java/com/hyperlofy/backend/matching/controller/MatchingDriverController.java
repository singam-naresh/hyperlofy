package com.hyperlofy.backend.matching.controller;

import com.hyperlofy.backend.matching.entity.MatchingRequest;
import com.hyperlofy.backend.matching.service.MatchingEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matching/driver")
@RequiredArgsConstructor
@Tag(name = "Matching Engine Driver API", description = "Endpoints for delivery partners to accept or reject incoming assignment offers")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class MatchingDriverController {

    private final MatchingEngineService matchingService;

    @PostMapping("/assignments/{id}/accept")
    @Operation(summary = "Accept Assignment Offer", description = "Delivery partner accepts dispatch offer, assigning them to the order.")
    public ResponseEntity<MatchingRequest> acceptAssignment(@PathVariable UUID id, @RequestParam UUID driverId) {
        return ResponseEntity.ok(matchingService.handleDriverResponse(id, driverId, true));
    }

    @PostMapping("/assignments/{id}/reject")
    @Operation(summary = "Reject Assignment Offer", description = "Delivery partner rejects dispatch offer, triggering automatic reassignment to next candidate.")
    public ResponseEntity<MatchingRequest> rejectAssignment(@PathVariable UUID id, @RequestParam UUID driverId) {
        return ResponseEntity.ok(matchingService.handleDriverResponse(id, driverId, false));
    }
}
