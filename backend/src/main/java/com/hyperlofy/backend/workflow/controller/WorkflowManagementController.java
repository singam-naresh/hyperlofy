package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowDefinition;
import com.hyperlofy.backend.workflow.service.WorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflow Definition Management API", description = "Register and manage reusable workflow process definitions for all Hyperlofy business domains")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class WorkflowManagementController {

    private final WorkflowEngineService workflowEngineService;

    @PostMapping
    @Operation(summary = "Register Workflow Definition",
            description = "Registers a new reusable BPM workflow definition — e.g. MERCHANT_REGISTRATION, KYC_APPROVAL, REFUND_APPROVAL, FRAUD_INVESTIGATION — with configurable timeout and retry policies.")
    public ResponseEntity<WorkflowDefinition> registerDefinition(
            @RequestParam String workflowKey,
            @RequestParam String workflowName,
            @RequestParam String workflowType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer timeoutHours,
            @RequestParam(required = false) Integer retryLimit) {
        return ResponseEntity.ok(workflowEngineService.registerWorkflowDefinition(
                workflowKey, workflowName, workflowType, description, timeoutHours, retryLimit));
    }

    @GetMapping
    @Operation(summary = "List All Workflow Definitions",
            description = "Returns all registered BPM workflow process definitions across all business domains.")
    public ResponseEntity<List<WorkflowDefinition>> listDefinitions() {
        return ResponseEntity.ok(workflowEngineService.getAllDefinitions());
    }
}
