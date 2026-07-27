package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.ai.forecasting.entity.MerchantDemandForecast;
import com.hyperlofy.backend.ai.forecasting.entity.MerchantIntelligenceSnapshot;
import com.hyperlofy.backend.ai.forecasting.service.DemandForecastingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/intelligence")
@RequiredArgsConstructor
@Tag(name = "Merchant AI Intelligence API", description = "Endpoints for merchant demand forecasting, inventory alerts, and sales projections")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantIntelligenceController {

    private final DemandForecastingService forecastingService;

    @GetMapping("/forecast")
    @Operation(summary = "Get Demand Forecast", description = "Retrieves predictive daily/weekly demand and revenue projections for the store.")
    public ResponseEntity<List<MerchantDemandForecast>> getDemandForecast(Principal principal) {
        return ResponseEntity.ok(forecastingService.getMerchantDemandForecast(UUID.randomUUID()));
    }

    @GetMapping("/snapshot")
    @Operation(summary = "Get Merchant Intelligence Snapshot", description = "Retrieves growth score, health score, repeat customer rate, and peak ordering hours.")
    public ResponseEntity<MerchantIntelligenceSnapshot> getMerchantSnapshot(Principal principal) {
        return ResponseEntity.ok(forecastingService.getMerchantIntelligence(UUID.randomUUID()));
    }

    @GetMapping("/inventory")
    @Operation(summary = "Get Inventory Intelligence", description = "Retrieves stock-out probability, restock recommendations, and dead inventory analytics.")
    public ResponseEntity<Map<String, Object>> getInventoryIntelligence(Principal principal) {
        return ResponseEntity.ok(forecastingService.getInventoryIntelligence(UUID.randomUUID()));
    }
}
