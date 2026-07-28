package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.CapacityForecast;
import com.hyperlofy.backend.platform.entity.ChaosExperiment;
import com.hyperlofy.backend.platform.entity.ProductionCertification;
import com.hyperlofy.backend.platform.service.PerformanceEngineeringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform/performance")
@RequiredArgsConstructor
@Tag(name = "Performance Engineering & Chaos Control API", description = "Endpoints for triggering chaos experiments, inspecting 365-day capacity growth forecasts, and issuing final production certification scorecards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PerformanceEngineeringController {

    private final PerformanceEngineeringService performanceService;

    @PostMapping("/chaos/simulate")
    @Operation(summary = "Simulate Chaos Experiment", description = "Injects controlled pod crash, latency injection, or Redis disconnect fault to validate resilience.")
    public ResponseEntity<ChaosExperiment> runChaos(@RequestParam String targetSystem, @RequestParam String faultType) {
        return ResponseEntity.ok(performanceService.runChaosExperiment(targetSystem, faultType));
    }

    @PostMapping("/certification")
    @Operation(summary = "Issue Production Certification", description = "Generates final production certification scorecard validating P95/P99 latencies, security audit, and DR readiness.")
    public ResponseEntity<ProductionCertification> issueCertification(@RequestParam String milestoneName) {
        return ResponseEntity.ok(performanceService.issueProductionCertification(milestoneName, "PRINCIPAL_PLATFORM_ARCHITECT"));
    }

    @GetMapping("/capacity/{resourceType}")
    @Operation(summary = "Get Capacity Growth Forecasts", description = "Returns 30, 90, 180, and 365-day capacity projections for Database, Redis, and Object Storage.")
    public ResponseEntity<List<CapacityForecast>> getForecasts(@PathVariable String resourceType) {
        return ResponseEntity.ok(performanceService.getCapacityForecasts(resourceType));
    }
}
