package com.hyperlofy.backend.matching.controller;

import com.hyperlofy.backend.matching.entity.MatchingAssignment;
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
@RequestMapping("/api/v1/matching/internal")
@RequiredArgsConstructor
@Tag(name = "Matching Engine Internal Integration API", description = "Endpoints for Unified Order Engine to initiate intelligent driver candidate scoring and dispatch matching")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MatchingInternalController {

    private final MatchingEngineService matchingService;

    @PostMapping("/requests")
    @Operation(summary = "Create Dispatch Matching Request", description = "Triggers intelligent candidate search and ranking for an active order.")
    public ResponseEntity<MatchingRequest> createRequest(
            @RequestParam UUID orderId,
            @RequestParam String orderType,
            @RequestParam Double pLat,
            @RequestParam Double pLng,
            @RequestParam Double dLat,
            @RequestParam Double dLng,
            @RequestParam(defaultValue = "NORMAL") String priority) {
        return ResponseEntity.ok(matchingService.createMatchingRequest(orderId, orderType, pLat, pLng, dLat, dLng, priority));
    }

    @PostMapping("/{id}/offer")
    @Operation(summary = "Send Offer to Candidate", description = "Dispatches assignment offer to top-ranked candidate driver.")
    public ResponseEntity<MatchingAssignment> sendOffer(@PathVariable UUID id, @RequestParam UUID driverId) {
        return ResponseEntity.ok(matchingService.sendOfferToCandidate(id, driverId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Matching Request State", description = "Inspects candidate ranking and current match status.")
    public ResponseEntity<MatchingRequest> getRequestState(@PathVariable UUID id) {
        return ResponseEntity.ok(matchingService.getMatchingRequest(id));
    }
}
