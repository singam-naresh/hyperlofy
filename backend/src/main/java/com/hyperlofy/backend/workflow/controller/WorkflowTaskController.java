package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowTask;
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
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Human Task Management API", description = "Manage human approval tasks — claim, complete, and delegate tasks with full SLA and candidate group support")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class WorkflowTaskController {

    private final WorkflowEngineService workflowEngineService;

    @GetMapping
    @Operation(summary = "Get All Pending Tasks",
            description = "Returns all PENDING workflow tasks across all business process instances available for claiming.")
    public ResponseEntity<List<WorkflowTask>> getAllPending() {
        return ResponseEntity.ok(workflowEngineService.getAllPendingTasks());
    }

    @GetMapping("/my")
    @Operation(summary = "Get My Assigned Tasks",
            description = "Returns all workflow tasks currently assigned to the requesting user — supports task inbox for operations staff.")
    public ResponseEntity<List<WorkflowTask>> getMyTasks(@RequestParam UUID userId) {
        return ResponseEntity.ok(workflowEngineService.getMyTasks(userId));
    }

    @GetMapping("/instance/{instanceId}")
    @Operation(summary = "Get Tasks by Workflow Instance",
            description = "Returns all tasks (pending, completed, compensation) for a specific workflow process instance.")
    public ResponseEntity<List<WorkflowTask>> getByInstance(@PathVariable UUID instanceId) {
        return ResponseEntity.ok(workflowEngineService.getTasksByInstance(instanceId));
    }

    @PostMapping("/{id}/claim")
    @Operation(summary = "Claim Task",
            description = "Claims an available PENDING task for the requesting user, transitioning it to CLAIMED status and setting claimedAt timestamp.")
    public ResponseEntity<WorkflowTask> claimTask(
            @PathVariable UUID id,
            @RequestParam UUID claimantUserId) {
        return ResponseEntity.ok(workflowEngineService.claimTask(id, claimantUserId));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete Task",
            description = "Marks a task as COMPLETED with a completion reason, and advances the parent workflow instance state machine to IN_PROGRESS.")
    public ResponseEntity<WorkflowTask> completeTask(
            @PathVariable UUID id,
            @RequestParam UUID actorUserId,
            @RequestParam(required = false) String completionReason) {
        return ResponseEntity.ok(workflowEngineService.completeTask(id, actorUserId, completionReason));
    }

    @PostMapping("/{id}/delegate")
    @Operation(summary = "Delegate Task",
            description = "Delegates a task from the current assignee to another user, recording delegation reason in audit history.")
    public ResponseEntity<WorkflowTask> delegateTask(
            @PathVariable UUID id,
            @RequestParam UUID fromUserId,
            @RequestParam UUID toUserId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(workflowEngineService.delegateTask(id, fromUserId, toUserId, comment));
    }
}
