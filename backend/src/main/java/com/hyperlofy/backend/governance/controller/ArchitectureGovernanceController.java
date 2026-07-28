package com.hyperlofy.backend.governance.controller;

import com.hyperlofy.backend.governance.entity.ArchitectureDecisionRecord;
import com.hyperlofy.backend.governance.entity.PlatformStandard;
import com.hyperlofy.backend.governance.service.PlatformGovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/governance")
@RequiredArgsConstructor
@Tag(name = "Architecture Governance & ADR Platform API", description = "Architecture Review Board governance, Architecture Decision Records (ADR), and platform architectural standards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ArchitectureGovernanceController {

    private final PlatformGovernanceService governanceService;

    @PostMapping("/architecture")
    @Operation(summary = "Register Platform Architectural Standard", description = "Registers architecture principles, reference standards (SOLID, DDD, Hexagonal), and design review policies.")
    public ResponseEntity<PlatformStandard> registerStandard(
            @RequestParam String standardKey,
            @RequestParam String standardName,
            @RequestParam String category,
            @RequestParam String description) {
        return ResponseEntity.ok(governanceService.registerStandard(standardKey, standardName, category, description));
    }

    @GetMapping("/architecture")
    @Operation(summary = "List All Platform Architecture Standards", description = "Returns active platform architectural standards, compliance scores, and category tags.")
    public ResponseEntity<List<PlatformStandard>> getStandards() {
        return ResponseEntity.ok(governanceService.getAllStandards());
    }

    @PostMapping("/adr")
    @Operation(summary = "Create Architecture Decision Record (ADR)", description = "Creates a new versioned Architecture Decision Record (ADR) with context, decision, consequences, and author attribution.")
    public ResponseEntity<ArchitectureDecisionRecord> createAdr(
            @RequestParam String adrCode,
            @RequestParam String title,
            @RequestParam UUID authorUserId,
            @RequestParam String context,
            @RequestParam String decision,
            @RequestParam(required = false) String consequences) {
        return ResponseEntity.ok(governanceService.createAdr(adrCode, title, authorUserId, context, decision, consequences));
    }

    @GetMapping("/adr")
    @Operation(summary = "List All Architecture Decision Records", description = "Returns complete architectural decision history (ADR-001 through latest), decision statuses, and consequences.")
    public ResponseEntity<List<ArchitectureDecisionRecord>> getAdrs() {
        return ResponseEntity.ok(governanceService.getAllAdrs());
    }
}
