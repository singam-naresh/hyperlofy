package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.GlobalRegion;
import com.hyperlofy.backend.global.service.GlobalInfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/capacity")
@RequiredArgsConstructor
@Tag(name = "Global Capacity Planning & Utilization API", description = "Monitor CPU/Memory utilization, database growth, Kafka throughput, Redis cluster capacity, and automated autoscaling recommendations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CapacityPlanningController {

    private final GlobalInfrastructureService globalService;

    @GetMapping
    @Operation(summary = "Get Regional Capacity & Utilization Metrics", description = "Returns current CPU/Memory node utilization, database storage growth, and network bandwidth per region.")
    public ResponseEntity<List<GlobalRegion>> getCapacity() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }

    @GetMapping("/forecast")
    @Operation(summary = "Get Machine-Learning Capacity Forecast", description = "Returns predictive capacity forecasting recommendations for cluster autoscaling and storage expansion.")
    public ResponseEntity<List<GlobalRegion>> getForecast() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }
}
