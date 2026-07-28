package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowAnalytics;
import com.hyperlofy.backend.workflow.entity.WorkflowEscalationPolicy;
import com.hyperlofy.backend.workflow.entity.WorkflowForm;
import com.hyperlofy.backend.workflow.service.WorkflowBpmEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow")
@RequiredArgsConstructor
@Tag(name = "Workflow Analytics, SLA & Forms API", description = "Enterprise process analytics (throughput, SLA compliance, automation ratio), dynamic form engine, and SLA escalation policy management")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class WorkflowAnalyticsController {

    private final WorkflowBpmEnterpriseService bpmEnterpriseService;

    // ── Analytics Endpoints ────────────────────────────────────────────────

    @PostMapping("/analytics")
    @Operation(summary = "Record Process Analytics",
            description = "Records workflow execution analytics for a given type and period — total instances, completion rate, avg execution hours, automation ratio, SLA compliance.")
    public ResponseEntity<WorkflowAnalytics> recordAnalytics(
            @RequestParam String workflowType,
            @RequestParam LocalDate periodDate,
            @RequestParam(required = false) Integer totalInstances,
            @RequestParam(required = false) Integer completedInstances,
            @RequestParam(required = false) Integer failedInstances,
            @RequestParam(required = false) Integer compensatedInstances,
            @RequestParam(required = false) BigDecimal avgExecutionHours,
            @RequestParam(required = false) BigDecimal avgHumanApprovalHours,
            @RequestParam(required = false) BigDecimal automationRatio,
            @RequestParam(required = false) BigDecimal slaComplianceRate) {
        return ResponseEntity.ok(bpmEnterpriseService.recordAnalytics(
                workflowType, periodDate, totalInstances, completedInstances,
                failedInstances, compensatedInstances, avgExecutionHours,
                avgHumanApprovalHours, automationRatio, slaComplianceRate));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get All Process Analytics",
            description = "Returns aggregated process analytics across all workflow types and periods.")
    public ResponseEntity<List<WorkflowAnalytics>> getAnalytics() {
        return ResponseEntity.ok(bpmEnterpriseService.getAllAnalytics());
    }

    @GetMapping("/analytics/{workflowType}")
    @Operation(summary = "Get Analytics by Workflow Type",
            description = "Returns historical execution metrics, SLA compliance, and automation ratio for a specific workflow type.")
    public ResponseEntity<List<WorkflowAnalytics>> getAnalyticsByType(@PathVariable String workflowType) {
        return ResponseEntity.ok(bpmEnterpriseService.getAnalyticsByType(workflowType));
    }

    // ── SLA / Escalation Policy Endpoints ─────────────────────────────────

    @PostMapping("/sla")
    @Operation(summary = "Register SLA Escalation Policy",
            description = "Configures multi-level SLA escalation policy — warning notification hours, breach hours, L1/L2/L3 escalation groups, and auto-cancel threshold.")
    public ResponseEntity<WorkflowEscalationPolicy> registerPolicy(
            @RequestParam String policyName,
            @RequestParam(required = false) String appliesToWorkflowType,
            @RequestParam(required = false) Integer warningHours,
            @RequestParam(required = false) Integer breachHours,
            @RequestParam String level1Group,
            @RequestParam(required = false) String level2Group,
            @RequestParam(required = false) String level3Group,
            @RequestParam(required = false) Integer autoCancelHours) {
        return ResponseEntity.ok(bpmEnterpriseService.registerEscalationPolicy(
                policyName, appliesToWorkflowType, warningHours, breachHours,
                level1Group, level2Group, level3Group, autoCancelHours));
    }

    @GetMapping("/sla")
    @Operation(summary = "Get Active SLA Escalation Policies",
            description = "Returns all active multi-level SLA escalation policies across workflow types.")
    public ResponseEntity<List<WorkflowEscalationPolicy>> getPolicies() {
        return ResponseEntity.ok(bpmEnterpriseService.getActivePolicies());
    }

    // ── Dynamic Form Engine Endpoints ──────────────────────────────────────

    @PostMapping("/forms")
    @Operation(summary = "Register Dynamic Form",
            description = "Registers a metadata-driven dynamic form schema — field types TEXT, NUMBER, DATE, BOOLEAN, DROPDOWN, MULTI_SELECT, FILE_UPLOAD, SIGNATURE — rendered without code changes.")
    public ResponseEntity<WorkflowForm> registerForm(
            @RequestParam String formKey,
            @RequestParam String formName,
            @RequestParam(required = false) String formType,
            @RequestParam String formSchema) {
        return ResponseEntity.ok(bpmEnterpriseService.registerForm(formKey, formName, formType, formSchema));
    }

    @GetMapping("/forms/{formKey}")
    @Operation(summary = "Get Dynamic Form Schema",
            description = "Returns JSON field schema for a dynamic form — client renders form from metadata.")
    public ResponseEntity<WorkflowForm> getForm(@PathVariable String formKey) {
        return ResponseEntity.ok(bpmEnterpriseService.getFormByKey(formKey));
    }
}
