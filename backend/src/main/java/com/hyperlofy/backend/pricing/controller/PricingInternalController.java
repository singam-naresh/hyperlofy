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
@RequestMapping("/api/v1/pricing/internal")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Internal Integration API", description = "Endpoints for Unified Order Engine to generate and recalculate order pricing quotes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PricingInternalController {

    private final DynamicPricingService pricingService;

    @PostMapping("/quote")
    @Operation(summary = "Generate Dynamic Pricing Quote", description = "Calculates base fare, distance/time charges, surge multiplier, and taxes for an order.")
    public ResponseEntity<PricingQuote> generateQuote(
            @RequestParam(required = false) UUID orderId,
            @RequestParam String serviceType,
            @RequestParam(defaultValue = "STANDARD") String serviceLevel,
            @RequestParam Double distanceKm,
            @RequestParam Integer estMinutes,
            @RequestParam(defaultValue = "1.0") Double surgeMultiplier) {
        return ResponseEntity.ok(pricingService.calculateQuote(orderId, serviceType, serviceLevel, distanceKm, estMinutes, surgeMultiplier));
    }

    @PostMapping("/recalculate")
    @Operation(summary = "Recalculate Active Quote", description = "Recalculates pricing quote upon route deviation, traffic delays, or trip duration changes.")
    public ResponseEntity<PricingQuote> recalculateQuote(
            @RequestParam UUID quoteId,
            @RequestParam Double newDistanceKm,
            @RequestParam Integer newDurationMinutes,
            @RequestParam String reason) {
        return ResponseEntity.ok(pricingService.recalculateQuote(quoteId, newDistanceKm, newDurationMinutes, reason));
    }

    @GetMapping("/quote/{id}")
    @Operation(summary = "Get Pricing Quote", description = "Retrieves quote breakdown and itemized fare components.")
    public ResponseEntity<PricingQuote> getQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(pricingService.getQuote(id));
    }
}
