package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.entity.AdminAction;
import com.hyperlofy.backend.admin.entity.AdminFeatureFlag;
import com.hyperlofy.backend.admin.entity.AdminIncident;
import com.hyperlofy.backend.admin.service.AdminOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/platform")
@RequiredArgsConstructor
@Tag(name = "Admin Platform Dashboard & Operations API", description = "Endpoints for platform administrators to oversee live orders, system health, incidents, and feature flags")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminPlatformController {

    private final AdminOperationsService adminService;

    @GetMapping("/incidents")
    @Operation(summary = "Get Active Operational Incidents", description = "Returns active platform outages, delivery incidents, or fraud alerts.")
    public ResponseEntity<List<AdminIncident>> getActiveIncidents() {
        return ResponseEntity.ok(adminService.getActiveIncidents());
    }

    @PostMapping("/incidents/report")
    @Operation(summary = "Report System Incident", description = "Opens SEV1/SEV2 operational incident case and alerts SRE on-call team.")
    public ResponseEntity<AdminIncident> reportIncident(
            @RequestParam String title,
            @RequestParam String incidentType,
            @RequestParam String severity,
            @RequestParam String reportedBy) {
        return ResponseEntity.ok(adminService.reportIncident(title, incidentType, severity, reportedBy));
    }

    @PutMapping("/feature-flags")
    @Operation(summary = "Toggle System Feature Flag", description = "Dynamically enables or disables platform feature flags with canary rollout percentages.")
    public ResponseEntity<AdminFeatureFlag> toggleFlag(
            @RequestParam String flagKey,
            @RequestParam Boolean isEnabled,
            @RequestParam(required = false) Integer rolloutPercentage) {
        return ResponseEntity.ok(adminService.toggleFeatureFlag(flagKey, isEnabled, rolloutPercentage));
    }

    @PostMapping("/action/log")
    @Operation(summary = "Log Operational Action Audit", description = "Records immutable audit entry for administrative manual overrides.")
    public ResponseEntity<AdminAction> logAction(
            @RequestParam String adminUser,
            @RequestParam String actionType,
            @RequestParam(required = false) UUID targetId,
            @RequestParam String description) {
        return ResponseEntity.ok(adminService.logAdminAction(adminUser, actionType, targetId, description));
    }
}
