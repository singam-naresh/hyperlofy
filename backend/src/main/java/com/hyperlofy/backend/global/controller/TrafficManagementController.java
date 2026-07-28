package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.DisasterRecoveryPlan;
import com.hyperlofy.backend.global.entity.TrafficRoutingPolicy;
import com.hyperlofy.backend.global.service.GlobalInfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/global")
@RequiredArgsConstructor
@Tag(name = "Global Traffic Management API", description = "Geo DNS routing, latency-based routing, weighted traffic distribution, automatic failover, and traffic failback")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class TrafficManagementController {

    private final GlobalInfrastructureService globalService;

    @PostMapping("/routing")
    @Operation(summary = "Configure Global Traffic Routing Policy", description = "Configures Geo DNS, latency-based, weighted, or health-based traffic routing policy for target region.")
    public ResponseEntity<TrafficRoutingPolicy> configureRouting(
            @RequestParam String policyName,
            @RequestParam(required = false) String routingType,
            @RequestParam String targetRegionCode,
            @RequestParam(required = false) Integer weightPercent) {
        return ResponseEntity.ok(globalService.configureRouting(policyName, routingType, targetRegionCode, weightPercent));
    }

    @GetMapping("/routing")
    @Operation(summary = "Get Active Traffic Routing Policies", description = "Returns active global traffic policies and target region distribution weights.")
    public ResponseEntity<List<TrafficRoutingPolicy>> getRouting() {
        return ResponseEntity.ok(globalService.getAllRoutingPolicies());
    }

    @PostMapping("/failover")
    @Operation(summary = "Trigger Cross-Region Traffic Failover", description = "Initiates immediate regional traffic failover from primary region to designated target DR region.")
    public ResponseEntity<DisasterRecoveryPlan> triggerFailover(@RequestParam String planName) {
        return ResponseEntity.ok(globalService.executeFailover(planName));
    }

    @PostMapping("/failback")
    @Operation(summary = "Trigger Cross-Region Traffic Failback", description = "Restores primary regional traffic routing after disaster recovery resolution.")
    public ResponseEntity<DisasterRecoveryPlan> triggerFailback(@RequestParam String planName) {
        return ResponseEntity.ok(globalService.executeFailover(planName));
    }
}
