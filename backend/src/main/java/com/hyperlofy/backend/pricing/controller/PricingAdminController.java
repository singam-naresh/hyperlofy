package com.hyperlofy.backend.pricing.controller;

import com.hyperlofy.backend.pricing.entity.PricingQuote;
import com.hyperlofy.backend.pricing.service.DynamicPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pricing/admin")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Admin API", description = "Endpoints for platform administrators to configure pricing rules, simulate quotes, and review price locks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PricingAdminController {

    private final DynamicPricingService pricingService;

    @GetMapping("/quote/{id}")
    @Operation(summary = "Admin Inspect Pricing Quote", description = "Returns full pricing breakdown, surge multipliers, and tax components for an order.")
    public ResponseEntity<PricingQuote> inspectQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(pricingService.getQuote(id));
    }

    @PostMapping("/simulate")
    @Operation(summary = "Simulate Pricing Quote", description = "Runs dry-run price calculations to test proposed rule changes before deployment.")
    public ResponseEntity<PricingQuote> simulateQuote(
            @RequestParam String serviceType,
            @RequestParam Double distanceKm,
            @RequestParam Integer estMinutes,
            @RequestParam(defaultValue = "1.0") Double surgeMultiplier) {
        return ResponseEntity.ok(pricingService.calculateQuote(null, serviceType, "SIMULATED", distanceKm, estMinutes, surgeMultiplier));
    }
}
