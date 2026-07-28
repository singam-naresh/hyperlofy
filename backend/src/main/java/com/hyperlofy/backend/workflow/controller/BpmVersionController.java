package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.WorkflowVersion;
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
@RequestMapping("/api/v1/bpm")
@RequiredArgsConstructor
@Tag(name = "BPM Version Management API", description = "Deploy BPMN 2.0 workflow versions, publish and archive versions with full governance lifecycle — comparable to Camunda Enterprise deployment")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class BpmVersionController {

    private final WorkflowBpmEnterpriseService bpmEnterpriseService;

    @PostMapping("/deploy")
    @Operation(summary = "Deploy BPMN Draft Version",
            description = "Deploys a new DRAFT workflow version for a definition. Supports raw BPMN 2.0 XML descriptor. Requires governance approval before activation.")
    public ResponseEntity<WorkflowVersion> deployDraft(
            @RequestParam UUID definitionId,
            @RequestParam(required = false) String bpmnXml,
            @RequestParam(required = false) String versionNotes) {
        return ResponseEntity.ok(bpmEnterpriseService.deployDraftVersion(definitionId, bpmnXml, versionNotes));
    }

    @PostMapping("/publish")
    @Operation(summary = "Publish Workflow Version",
            description = "Publishes a DRAFT version to ACTIVE, automatically archiving the previously active version. Implements workflow governance publish gate.")
    public ResponseEntity<WorkflowVersion> publish(
            @RequestParam UUID versionId,
            @RequestParam UUID publishedBy) {
        return ResponseEntity.ok(bpmEnterpriseService.publishVersion(versionId, publishedBy));
    }

    @GetMapping("/versions")
    @Operation(summary = "List All Versions for a Definition",
            description = "Returns version history (DRAFT, ACTIVE, ARCHIVED) for a workflow definition.")
    public ResponseEntity<List<WorkflowVersion>> listVersions(@RequestParam UUID definitionId) {
        return ResponseEntity.ok(bpmEnterpriseService.getVersionsByDefinition(definitionId));
    }
}
