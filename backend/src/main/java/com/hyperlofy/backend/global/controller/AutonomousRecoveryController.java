package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.AutonomousRecoveryExecution;
import com.hyperlofy.backend.global.service.GlobalEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/autonomous")
@RequiredArgsConstructor
@Tag(name = "Autonomous Self-Healing Infrastructure API", description = "AI-powered predictive failover, automated service pod restarts, node replacements, secret rotation, and audited self-healing actions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AutonomousRecoveryController {

    private final GlobalEnterpriseService enterpriseService;

    @PostMapping("/recovery")
    @Operation(summary = "Trigger Autonomous Self-Healing Remediation", description = "Triggers automated service recovery (RESTART_POD, REPLACE_NODE, REDIS_FAILOVER, SECRET_ROTATE) with full audit logging.")
    public ResponseEntity<AutonomousRecoveryExecution> triggerRecovery(
            @RequestParam String executionCode,
            @RequestParam String targetService,
            @RequestParam String regionCode,
            @RequestParam(required = false) String actionType,
            @RequestParam String triggerReason) {
        return ResponseEntity.ok(enterpriseService.executeAutonomousRecovery(executionCode, targetService, regionCode, actionType, triggerReason));
    }

    @GetMapping("/recovery/history")
    @Operation(summary = "Get Autonomous Self-Healing Execution History", description = "Returns historical self-healing remediation logs, execution durations, and recovery audit records.")
    public ResponseEntity<List<AutonomousRecoveryExecution>> getHistory() {
        return ResponseEntity.ok(enterpriseService.getRecoveryHistory());
    }
}
