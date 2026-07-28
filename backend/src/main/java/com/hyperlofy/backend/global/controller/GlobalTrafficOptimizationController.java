package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.GlobalTrafficOptimization;
import com.hyperlofy.backend.global.service.GlobalEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/global/traffic")
@RequiredArgsConstructor
@Tag(name = "Intelligent Global Traffic Optimization API", description = "AI routing decisions, latency optimization, cost-aware & carbon-aware traffic shifting, and congestion avoidance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class GlobalTrafficOptimizationController {

    private final GlobalEnterpriseService enterpriseService;

    @PostMapping("/optimize")
    @Operation(summary = "Optimize Global Regional Traffic", description = "Executes AI-powered dynamic traffic shifting between regions for latency reduction, cost saving, or carbon minimization.")
    public ResponseEntity<GlobalTrafficOptimization> optimizeTraffic(
            @RequestParam String optimizationCode,
            @RequestParam String sourceRegion,
            @RequestParam String targetRegion,
            @RequestParam(required = false) Integer shiftedPercent,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(enterpriseService.optimizeTraffic(optimizationCode, sourceRegion, targetRegion, shiftedPercent, reason));
    }
}
