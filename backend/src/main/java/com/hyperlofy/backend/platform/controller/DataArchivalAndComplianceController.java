package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.ArchiveCatalog;
import com.hyperlofy.backend.platform.entity.LegalHold;
import com.hyperlofy.backend.platform.entity.RetentionPolicy;
import com.hyperlofy.backend.platform.service.DataArchivalAndComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform/archival")
@RequiredArgsConstructor
@Tag(name = "Data Archival, Retention & Compliance API", description = "Endpoints for applying legal holds, triggering automated data archival jobs, and inspecting retention policy classes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DataArchivalAndComplianceController {

    private final DataArchivalAndComplianceService archivalService;

    @PostMapping("/legal-holds")
    @Operation(summary = "Apply Legal Hold", description = "Applies legal hold on target record ID preventing deletion, modification, or automated purging.")
    public ResponseEntity<LegalHold> applyLegalHold(
            @RequestParam String caseId,
            @RequestParam String targetTable,
            @RequestParam String recordId,
            @RequestParam String reason,
            @RequestParam String owner) {
        return ResponseEntity.ok(archivalService.applyLegalHold(caseId, targetTable, recordId, reason, owner));
    }

    @GetMapping("/legal-holds/check")
    @Operation(summary = "Check Legal Hold Status", description = "Verifies if a specific record ID is actively protected under a legal hold case.")
    public ResponseEntity<Map<String, Object>> checkLegalHold(@RequestParam String targetTable, @RequestParam String recordId) {
        boolean underHold = archivalService.isRecordUnderLegalHold(targetTable, recordId);
        return ResponseEntity.ok(Map.of("targetTable", targetTable, "recordId", recordId, "isUnderLegalHold", underHold));
    }

    @PostMapping("/archive")
    @Operation(summary = "Execute Data Archival", description = "Executes compressed AES-256 encrypted data archival for historical dataset batches.")
    public ResponseEntity<ArchiveCatalog> executeArchival(
            @RequestParam String datasetName,
            @RequestParam int recordCount,
            @RequestParam String location) {
        return ResponseEntity.ok(archivalService.executeDataArchival(datasetName, recordCount, location, "ADMIN_OPERATOR"));
    }

    @GetMapping("/retention/{policyName}")
    @Operation(summary = "Get Retention Policy", description = "Fetches configuration, retention period, and storage tiering for a policy class.")
    public ResponseEntity<RetentionPolicy> getRetentionPolicy(@PathVariable String policyName) {
        return ResponseEntity.ok(archivalService.getRetentionPolicy(policyName));
    }
}
