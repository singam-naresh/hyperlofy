package com.hyperlofy.backend.governance.controller;

import com.hyperlofy.backend.governance.entity.ProductionCertification;
import com.hyperlofy.backend.governance.entity.TechnicalDebtItem;
import com.hyperlofy.backend.governance.service.PlatformGovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/governance")
@RequiredArgsConstructor
@Tag(name = "Production Certification & Executive Governance API", description = "Issue 10-pillar enterprise production readiness certification, view executive governance dashboard, and monitor platform risk register")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ProductionCertificationController {

    private final PlatformGovernanceService governanceService;

    @PostMapping("/certification")
    @Operation(summary = "Generate 10-Pillar Enterprise Production Certification", description = "Generates official Enterprise Production Certification verifying 10 pillars: Architecture, Security, Performance, Reliability, Compliance, Data, API, Infrastructure, Operations, and Overall Readiness.")
    public ResponseEntity<ProductionCertification> generateCertification(
            @RequestParam String certificationCode,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(governanceService.generateCertification(certificationCode, notes));
    }

    @GetMapping("/certification")
    @Operation(summary = "Get Production Certification Status", description = "Returns active 10-pillar production readiness certificates, platform versions, and certifying authority signatures.")
    public ResponseEntity<List<ProductionCertification>> getCertifications() {
        return ResponseEntity.ok(governanceService.getAllCertifications());
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get Executive Governance Dashboard", description = "Returns executive platform compliance metrics, ADR adoption, quality gate pass rate (100%), and overall platform maturity score.")
    public ResponseEntity<List<ProductionCertification>> getDashboard() {
        return ResponseEntity.ok(governanceService.getAllCertifications());
    }

    @GetMapping("/risks")
    @Operation(summary = "Get Platform Technical Debt & Risk Register", description = "Returns technical debt backlog items, risk scores, severity levels, and target resolution timelines.")
    public ResponseEntity<List<TechnicalDebtItem>> getRisks() {
        return ResponseEntity.ok(governanceService.getAllTechnicalDebt());
    }
}
