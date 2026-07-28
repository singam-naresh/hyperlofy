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
@RequestMapping("/api/v1/pricing/marketplace")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Marketplace API", description = "Endpoints for calculating marketplace order delivery fares")
@PreAuthorize("hasRole('CUSTOMER')")
public class PricingMarketplaceController {

    private final DynamicPricingService pricingService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate Marketplace Delivery Fare", description = "Returns upfront delivery quote for marketplace merchant orders.")
    public ResponseEntity<PricingQuote> calculateMarketplaceFare(
            @RequestParam Double distanceKm,
            @RequestParam Integer estMinutes,
            @RequestParam(defaultValue = "1.0") Double surgeMultiplier) {
        return ResponseEntity.ok(pricingService.calculateQuote(null, "MARKETPLACE", "STANDARD", distanceKm, estMinutes, surgeMultiplier));
    }
}
