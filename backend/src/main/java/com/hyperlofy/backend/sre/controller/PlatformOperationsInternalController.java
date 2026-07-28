package com.hyperlofy.backend.sre.controller;

import com.hyperlofy.backend.sre.entity.PlatformDeployment;
import com.hyperlofy.backend.sre.entity.PlatformHealth;
import com.hyperlofy.backend.sre.entity.PlatformIncident;
import com.hyperlofy.backend.sre.entity.PlatformSlo;
import com.hyperlofy.backend.sre.service.PlatformOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sre/internal")
@RequiredArgsConstructor
@Tag(name = "SRE & Cloud Operations Internal API", description = "Endpoints for automated health probes, canary deployment triggers, incident reporting, and SLO budget monitoring")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PlatformOperationsInternalController {

    private final PlatformOperationsService sreService;

    @PostMapping("/health/probe")
    @Operation(summary = "Update Service Health Probe", description = "Updates CPU/memory utilization and P95 latency statistics for microservice instance.")
    public ResponseEntity<PlatformHealth> probeHealth(
            @RequestParam String serviceName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal cpuPct,
            @RequestParam(required = false) BigDecimal memPct,
            @RequestParam(required = false) Integer latencyMs) {
        return ResponseEntity.ok(sreService.updateHealthProbe(serviceName, status, cpuPct, memPct, latencyMs));
    }

    @PostMapping("/deploy")
    @Operation(summary = "Trigger Automated Service Deployment", description = "Executes canary, blue/green, or rolling deployment strategy for Hyperlofy microservice.")
    public ResponseEntity<PlatformDeployment> triggerDeploy(
            @RequestParam String serviceName,
            @RequestParam String version,
            @RequestParam(required = false) String strategy,
            @RequestParam String deployedBy) {
        return ResponseEntity.ok(sreService.triggerDeployment(serviceName, version, strategy, deployedBy));
    }

    @PostMapping("/incidents/open")
    @Operation(summary = "Open SRE Incident", description = "Triggers SEV1/SEV2 incident alert for on-call SRE and DevOps engineering team.")
    public ResponseEntity<PlatformIncident> openIncident(
            @RequestParam String serviceName,
            @RequestParam(required = false) String severity,
            @RequestParam String description) {
        return ResponseEntity.ok(sreService.openIncident(serviceName, severity, description));
    }

    @GetMapping("/slos")
    @Operation(summary = "Get SLO Error Budget Compliance", description = "Returns target vs actual SLA percentages and remaining error budget percentages.")
    public ResponseEntity<List<PlatformSlo>> getSlos() {
        return ResponseEntity.ok(sreService.getSloCompliance());
    }
}
