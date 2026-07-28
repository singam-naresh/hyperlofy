package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowHistory;
import com.hyperlofy.backend.workflow.entity.WorkflowInstance;
import com.hyperlofy.backend.workflow.service.WorkflowEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflow-instances")
@RequiredArgsConstructor
@Tag(name = "Workflow Instance Execution API", description = "Start, approve, reject, cancel, and query workflow process instances with state machine transitions and Saga compensation")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class WorkflowInstanceController {

    private final WorkflowEngineService workflowEngineService;

    @PostMapping
    @Operation(summary = "Start Workflow Instance",
            description = "Starts a new business process instance (order approval, KYC, refund chain, fraud investigation) against a registered workflow definition. Automatically creates first human approval task.")
    public ResponseEntity<WorkflowInstance> startWorkflow(
            @RequestParam UUID definitionId,
            @RequestParam String instanceRef,
            @RequestParam UUID initiatorUserId,
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String correlationKey,
            @RequestParam(required = false) String businessContext,
            @RequestParam(required = false) Integer timeoutHours) {
        return ResponseEntity.ok(workflowEngineService.startWorkflow(
                definitionId, instanceRef, initiatorUserId, tenantId,
                priority, correlationKey, businessContext, timeoutHours));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Workflow Instance",
            description = "Returns current state, priority, SLA due date, and retry count of a specific workflow process instance.")
    public ResponseEntity<WorkflowInstance> getInstance(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowEngineService.getWorkflowInstance(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve Workflow",
            description = "Transitions workflow to APPROVED state, completes all pending tasks, and records audit trail entry.")
    public ResponseEntity<WorkflowInstance> approve(
            @PathVariable UUID id,
            @RequestParam UUID actorUserId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(workflowEngineService.approveWorkflow(id, actorUserId, comment));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject Workflow",
            description = "Transitions workflow to REJECTED state, completes all pending tasks, and records rejection audit trail.")
    public ResponseEntity<WorkflowInstance> reject(
            @PathVariable UUID id,
            @RequestParam UUID actorUserId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(workflowEngineService.rejectWorkflow(id, actorUserId, comment));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel Workflow",
            description = "Cancels an active workflow instance, terminates all pending tasks, and records cancellation history.")
    public ResponseEntity<WorkflowInstance> cancel(
            @PathVariable UUID id,
            @RequestParam UUID actorUserId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(workflowEngineService.cancelWorkflow(id, actorUserId, comment));
    }

    @PostMapping("/{id}/compensate")
    @Operation(summary = "Execute Saga Compensation",
            description = "Triggers distributed Saga rollback, creating a SAGA_STEP compensation task and transitioning the workflow to COMPENSATED state.")
    public ResponseEntity<WorkflowInstance> compensate(
            @PathVariable UUID id,
            @RequestParam UUID actorUserId) {
        return ResponseEntity.ok(workflowEngineService.executeCompensation(id, actorUserId));
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "List Instances by State",
            description = "Returns all workflow instances in a given state — e.g. WAITING_APPROVAL, IN_PROGRESS, FAILED.")
    public ResponseEntity<List<WorkflowInstance>> getByState(@PathVariable String state) {
        return ResponseEntity.ok(workflowEngineService.getInstancesByState(state));
    }

    @GetMapping("/{id}/audit")
    @Operation(summary = "Get Workflow Audit Trail",
            description = "Returns chronological audit trail of all state transitions, approvals, rejections, and task events for a workflow instance.")
    public ResponseEntity<List<WorkflowHistory>> getAuditTrail(@PathVariable UUID id) {
        return ResponseEntity.ok(workflowEngineService.getAuditTrail(id));
    }
}
