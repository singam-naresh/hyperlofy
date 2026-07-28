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
@RequestMapping("/api/v1/pricing/buyforme")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Buy For Me API", description = "Endpoints for calculating assisted purchasing service and delivery fares")
@PreAuthorize("hasRole('CUSTOMER')")
public class PricingBuyForMeController {

    private final DynamicPricingService pricingService;

    @PostMapping("/calculate")
    @Operation(summary = "Calculate Buy For Me Service Fare", description = "Calculates purchasing fee, delivery distance fare, and time charges.")
    public ResponseEntity<PricingQuote> calculateBuyForMeFare(
            @RequestParam Double distanceKm,
            @RequestParam Integer estMinutes,
            @RequestParam(defaultValue = "1.0") Double surgeMultiplier) {
        return ResponseEntity.ok(pricingService.calculateQuote(null, "BUY_FOR_ME", "EXPRESS", distanceKm, estMinutes, surgeMultiplier));
    }
}
