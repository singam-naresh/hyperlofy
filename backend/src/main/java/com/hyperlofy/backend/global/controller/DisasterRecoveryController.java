package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.DisasterRecoveryPlan;
import com.hyperlofy.backend.global.service.GlobalInfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dr")
@RequiredArgsConstructor
@Tag(name = "Disaster Recovery Orchestration API", description = "RPO/RTO plan configuration, automated disaster declaration, dependency ordering, and DR drill execution")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DisasterRecoveryController {

    private final GlobalInfrastructureService globalService;

    @PostMapping("/plans")
    @Operation(summary = "Create Disaster Recovery Plan", description = "Configures automated DR plan specifying primary region, target DR region, target RPO (seconds), and target RTO (seconds).")
    public ResponseEntity<DisasterRecoveryPlan> createPlan(
            @RequestParam String planName,
            @RequestParam String primaryRegionCode,
            @RequestParam String targetDrRegionCode,
            @RequestParam(required = false) Integer rpoSeconds,
            @RequestParam(required = false) Integer rtoSeconds) {
        return ResponseEntity.ok(globalService.createDrPlan(planName, primaryRegionCode, targetDrRegionCode, rpoSeconds, rtoSeconds));
    }

    @GetMapping("/plans")
    @Operation(summary = "List All Disaster Recovery Plans", description = "Returns active DR plans, target RPO/RTO metrics, and last automated drill timestamp.")
    public ResponseEntity<List<DisasterRecoveryPlan>> getPlans() {
        return ResponseEntity.ok(globalService.getAllDrPlans());
    }

    @PostMapping("/execute")
    @Operation(summary = "Execute Automated Disaster Recovery Runbook", description = "Executes automated DR recovery plan including PostgreSQL promotion, Kafka consumer failover, Redis cluster promotion, and DNS switching.")
    public ResponseEntity<DisasterRecoveryPlan> executePlan(@RequestParam String planName) {
        return ResponseEntity.ok(globalService.executeFailover(planName));
    }

    @GetMapping("/executions")
    @Operation(summary = "Get Historical DR Plan Executions & Drills", description = "Returns historical disaster recovery drill executions, recovery timelines, and compliance validation reports.")
    public ResponseEntity<List<DisasterRecoveryPlan>> getExecutions() {
        return ResponseEntity.ok(globalService.getAllDrPlans());
    }
}
