package com.hyperlofy.backend.sre.controller;

import com.hyperlofy.backend.sre.entity.PlatformCapacityForecast;
import com.hyperlofy.backend.sre.entity.PlatformReleaseHistory;
import com.hyperlofy.backend.sre.entity.PlatformRunbook;
import com.hyperlofy.backend.sre.entity.PlatformSecurityEvent;
import com.hyperlofy.backend.sre.service.PlatformEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sre/enterprise")
@RequiredArgsConstructor
@Tag(name = "SRE & Cloud Operations Enterprise Addendum API", description = "Endpoints for Automated Rollbacks, Capacity Forecasting, Runtime Policy Enforcement, and Self-Healing Infrastructure Runbooks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PlatformEnterpriseController {

    private final PlatformEnterpriseService enterpriseService;

    @PostMapping("/rollback")
    @Operation(summary = "Execute Automated Service Rollback", description = "Triggers automatic rollback to last stable microservice release upon canary SLA degradation.")
    public ResponseEntity<PlatformReleaseHistory> executeRollback(
            @RequestParam String serviceName,
            @RequestParam String version,
            @RequestParam String approvalBy) {
        return ResponseEntity.ok(enterpriseService.recordRollbackExecution(serviceName, version, approvalBy));
    }

    @PostMapping("/capacity/forecast")
    @Operation(summary = "Generate Infrastructure Capacity Forecast", description = "Forecasts node utilization trends, cluster scaling recommendations, and resource optimization parameters.")
    public ResponseEntity<PlatformCapacityForecast> generateForecast(
            @RequestParam String clusterName,
            @RequestParam String resourceType,
            @RequestParam BigDecimal currentPct,
            @RequestParam BigDecimal forecastPct,
            @RequestParam(required = false) Integer recommendedNodes) {
        return ResponseEntity.ok(enterpriseService.generateCapacityForecast(clusterName, resourceType, currentPct, forecastPct, recommendedNodes));
    }

    @PostMapping("/security/log")
    @Operation(summary = "Record Container Runtime Security Event", description = "Logs runtime policy violations, admission controller rejections, or certificate security alerts.")
    public ResponseEntity<PlatformSecurityEvent> logSecurity(
            @RequestParam String eventCode,
            @RequestParam String component,
            @RequestParam(required = false) String severity,
            @RequestParam String description) {
        return ResponseEntity.ok(enterpriseService.logSecurityEvent(eventCode, component, severity, description));
    }

    @PostMapping("/runbook/execute")
    @Operation(summary = "Trigger Self-Healing Infrastructure Runbook", description = "Executes automated recovery workflows for auto-restart, cluster rebalancing, or configuration drift remediation.")
    public ResponseEntity<PlatformRunbook> executeRunbook(
            @RequestParam String runbookName,
            @RequestParam String triggerCondition) {
        return ResponseEntity.ok(enterpriseService.executeAutomatedRunbook(runbookName, triggerCondition));
    }

    @GetMapping("/capacity/cluster/{clusterName}")
    @Operation(summary = "Get Cluster Capacity Projections", description = "Returns CPU/memory utilization forecasts and node scaling recommendations for targeted Kubernetes cluster.")
    public ResponseEntity<List<PlatformCapacityForecast>> getClusterCapacity(@PathVariable String clusterName) {
        return ResponseEntity.ok(enterpriseService.getCapacityForecasts(clusterName));
    }
}
