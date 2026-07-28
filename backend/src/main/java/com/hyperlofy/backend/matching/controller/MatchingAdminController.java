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
@RequestMapping("/api/v1/matching/admin")
@RequiredArgsConstructor
@Tag(name = "Matching Engine Admin API", description = "Endpoints for platform administrators to inspect matching requests and override driver assignments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class MatchingAdminController {

    private final MatchingEngineService matchingService;

    @GetMapping("/{id}")
    @Operation(summary = "Inspect Matching Request", description = "Returns complete matching request state and score breakdown.")
    public ResponseEntity<MatchingRequest> inspectRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(matchingService.getMatchingRequest(id));
    }

    @PostMapping("/{id}/override")
    @Operation(summary = "Admin Force Override Assignment", description = "Allows administrators to force-assign a specific driver to an order.")
    public ResponseEntity<MatchingRequest> overrideAssignment(@PathVariable UUID id, @RequestParam UUID driverId) {
        return ResponseEntity.ok(matchingService.handleDriverResponse(id, driverId, true));
    }
}
