package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.DrFailoverLog;
import com.hyperlofy.backend.platform.entity.DrRecoveryMetric;
import com.hyperlofy.backend.platform.entity.DrRunbook;
import com.hyperlofy.backend.platform.entity.IncidentRecord;
import com.hyperlofy.backend.platform.service.DisasterRecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/platform/dr")
@RequiredArgsConstructor
@Tag(name = "Disaster Recovery & High Availability API", description = "Endpoints for triggering manual failover, reporting operational incidents, and inspecting operational runbooks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DisasterRecoveryHealthController {

    private final DisasterRecoveryService drService;

    @PostMapping("/failover")
    @Operation(summary = "Trigger System Failover", description = "Initiates manual failover for Database, Redis, Gateway, or Payment Provider.")
    public ResponseEntity<DrFailoverLog> triggerFailover(
            @RequestParam String targetSystem,
            @RequestParam String oldNode,
            @RequestParam String newNode,
            @RequestParam String reason) {
        return ResponseEntity.ok(drService.executeFailover(targetSystem, oldNode, newNode, reason, "ADMIN_OPERATOR", false));
    }

    @PostMapping("/incidents")
    @Operation(summary = "Report Operational Incident", description = "Records a SEV1/SEV2/SEV3/SEV4 incident and starts MTTR tracking.")
    public ResponseEntity<IncidentRecord> reportIncident(
            @RequestParam String title,
            @RequestParam String severity,
            @RequestParam String rootCause) {
        return ResponseEntity.ok(drService.reportIncident(title, severity, rootCause));
    }

    @GetMapping("/targets/{serviceName}")
    @Operation(summary = "Get RTO/RPO Recovery Targets", description = "Fetches RTO and RPO target thresholds for a specific domain service.")
    public ResponseEntity<DrRecoveryMetric> getRecoveryTarget(@PathVariable String serviceName) {
        return ResponseEntity.ok(drService.getServiceRecoveryTarget(serviceName));
    }

    @GetMapping("/runbooks")
    @Operation(summary = "Get Operational Runbooks", description = "Lists operational runbooks and recovery instructions.")
    public ResponseEntity<List<DrRunbook>> getRunbooks() {
        return ResponseEntity.ok(drService.getAllRunbooks());
    }
}
