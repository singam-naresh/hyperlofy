package com.hyperlofy.backend.pricing.controller;

import com.hyperlofy.backend.pricing.entity.PricingQuote;
import com.hyperlofy.backend.pricing.service.DynamicPricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pricing/pickupdrop")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Pickup & Drop API", description = "Endpoints for calculating point-to-point courier parcel delivery fares")
@PreAuthorize("hasRole('CUSTOMER')")
public class PricingPickupDropController {

    private final DynamicPricingService pricingService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate Pickup & Drop Courier Fare", description = "Calculates parcel weight/size handling fee and distance fare.")
    public ResponseEntity<PricingQuote> calculatePickupDropFare(
            @RequestParam Double distanceKm,
            @RequestParam Integer estMinutes,
            @RequestParam(defaultValue = "1.0") Double surgeMultiplier) {
        return ResponseEntity.ok(pricingService.calculateQuote(null, "PICKUP_DROP", "STANDARD", distanceKm, estMinutes, surgeMultiplier));
    }
}
