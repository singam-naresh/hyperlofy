package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowCase;
import com.hyperlofy.backend.workflow.entity.WorkflowCaseNote;
import com.hyperlofy.backend.workflow.service.WorkflowBpmEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Tag(name = "Case Management API", description = "Adaptive case management for long-running investigations — Fraud Investigation, Compliance Review, Customer Complaints, Chargeback Review, Merchant Suspension")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CaseManagementController {

    private final WorkflowBpmEnterpriseService bpmEnterpriseService;

    @PostMapping
    @Operation(summary = "Open Case",
            description = "Opens a new investigation case (FRAUD_INVESTIGATION, COMPLIANCE_INVESTIGATION, CUSTOMER_COMPLAINT, CHARGEBACK_REVIEW) with subject assignment and SLA due date.")
    public ResponseEntity<WorkflowCase> openCase(
            @RequestParam String caseRef,
            @RequestParam String caseType,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) UUID subjectUserId,
            @RequestParam(required = false) UUID assigneeUserId,
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID relatedWorkflowInstanceId) {
        return ResponseEntity.ok(bpmEnterpriseService.openCase(
                caseRef, caseType, title, description, subjectUserId, assigneeUserId, tenantId, priority, relatedWorkflowInstanceId));
    }

    @GetMapping
    @Operation(summary = "List All Cases",
            description = "Returns all active and historical investigation cases across all types.")
    public ResponseEntity<List<WorkflowCase>> getAllCases() {
        return ResponseEntity.ok(bpmEnterpriseService.getAllCases());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Case by ID",
            description = "Returns full case details including status, priority, subject, assignee, and resolution.")
    public ResponseEntity<WorkflowCase> getCase(@PathVariable UUID id) {
        return ResponseEntity.ok(bpmEnterpriseService.getCaseById(id));
    }

    @PostMapping("/{id}/note")
    @Operation(summary = "Add Case Note or Evidence",
            description = "Adds an investigation note, evidence, decision, or attachment to an open case.")
    public ResponseEntity<WorkflowCaseNote> addNote(
            @PathVariable UUID id,
            @RequestParam UUID authorUserId,
            @RequestParam(required = false) String noteType,
            @RequestParam String content,
            @RequestParam(required = false) String attachmentUrl,
            @RequestParam(required = false, defaultValue = "false") boolean isInternal) {
        return ResponseEntity.ok(bpmEnterpriseService.addCaseNote(id, authorUserId, noteType, content, attachmentUrl, isInternal));
    }

    @GetMapping("/{id}/notes")
    @Operation(summary = "Get Case Notes",
            description = "Returns chronological notes, evidence, and decisions recorded on a case.")
    public ResponseEntity<List<WorkflowCaseNote>> getNotes(@PathVariable UUID id) {
        return ResponseEntity.ok(bpmEnterpriseService.getCaseNotes(id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close Case",
            description = "Closes a case with a resolution summary, transitioning it to CLOSED status.")
    public ResponseEntity<WorkflowCase> closeCase(
            @PathVariable UUID id,
            @RequestParam(required = false) String resolution) {
        return ResponseEntity.ok(bpmEnterpriseService.closeCase(id, resolution));
    }
}
