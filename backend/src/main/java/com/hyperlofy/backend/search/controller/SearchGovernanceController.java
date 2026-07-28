package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.SearchGovernance;
import com.hyperlofy.backend.search.service.SearchEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search/governance")
@RequiredArgsConstructor
@Tag(name = "Search Governance & Security Classification API", description = "Document classification, sensitivity levels (PUBLIC, INTERNAL, RESTRICTED, CONFIDENTIAL), access roles, and search audit history")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SearchGovernanceController {

    private final SearchEnterpriseService enterpriseService;

    @PostMapping("/classify")
    @Operation(summary = "Classify Document Governance & Sensitivity", description = "Applies security sensitivity level (PUBLIC, INTERNAL, RESTRICTED, CONFIDENTIAL) and access role restrictions to indexed documents.")
    public ResponseEntity<SearchGovernance> classify(
            @RequestParam String documentId,
            @RequestParam(required = false) String sensitivityLevel,
            @RequestParam(required = false) String classification,
            @RequestParam UUID ownerUserId,
            @RequestParam(required = false) String accessRoles) {
        return ResponseEntity.ok(enterpriseService.classifyDocument(documentId, sensitivityLevel, classification, ownerUserId, accessRoles));
    }

    @GetMapping
    @Operation(summary = "Get Document Governance Classification", description = "Returns security sensitivity classification, owner, and access role requirements for document ID.")
    public ResponseEntity<SearchGovernance> getGovernance(@RequestParam String documentId) {
        return ResponseEntity.ok(enterpriseService.getGovernanceByDocId(documentId));
    }
}
