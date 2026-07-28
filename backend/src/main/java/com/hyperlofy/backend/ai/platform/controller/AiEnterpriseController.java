package com.hyperlofy.backend.ai.platform.controller;

import com.hyperlofy.backend.ai.platform.entity.AiAgentRun;
import com.hyperlofy.backend.ai.platform.entity.AiGovernance;
import com.hyperlofy.backend.ai.platform.entity.AiMemoryStore;
import com.hyperlofy.backend.ai.platform.entity.AiSafetyEvent;
import com.hyperlofy.backend.ai.platform.service.AiEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai/platform/enterprise")
@RequiredArgsConstructor
@Tag(name = "AI Platform Enterprise Addendum API", description = "Endpoints for AI Governance, Prompt Injection Detection, Autonomous Multi-Agent Workflows, and Long-Term Memory Persistence")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AiEnterpriseController {

    private final AiEnterpriseService enterpriseService;

    @PostMapping("/models/approve")
    @Operation(summary = "Approve Model for Production Use", description = "Validates model safety and policy compliance before releasing to production routing.")
    public ResponseEntity<AiGovernance> approveModel(
            @RequestParam String modelName,
            @RequestParam String approvedBy,
            @RequestParam(required = false) String policyVersion) {
        return ResponseEntity.ok(enterpriseService.approveModel(modelName, approvedBy, policyVersion));
    }

    @PostMapping("/safety/check")
    @Operation(summary = "Record Prompt Safety Violation", description = "Logs prompt injection, PII leak, or sensitive content violations.")
    public ResponseEntity<AiSafetyEvent> recordSafetyViolation(
            @RequestParam(required = false) UUID userId,
            @RequestParam String violationType,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String sanitizedPrompt) {
        return ResponseEntity.ok(enterpriseService.recordSafetyViolation(userId, violationType, severity, sanitizedPrompt));
    }

    @PostMapping("/agents/run")
    @Operation(summary = "Log Autonomous Agent Workflow Run", description = "Logs multi-step task planning, tool calls, and execution steps for autonomous AI agents.")
    public ResponseEntity<AiAgentRun> recordAgentExecution(
            @RequestParam String agentName,
            @RequestParam String taskGoal,
            @RequestBody(required = false) String stepsJson) {
        return ResponseEntity.ok(enterpriseService.recordAgentExecution(agentName, taskGoal, stepsJson));
    }

    @PostMapping("/memory/store")
    @Operation(summary = "Store User Context in Long-Term Memory", description = "Persists user preferences, context hooks, or vector memory for session continuity.")
    public ResponseEntity<AiMemoryStore> storeMemory(
            @RequestParam UUID userId,
            @RequestParam String key,
            @RequestBody String value,
            @RequestParam(required = false) String memoryType) {
        return ResponseEntity.ok(enterpriseService.storeLongTermMemory(userId, key, value, memoryType));
    }
}
