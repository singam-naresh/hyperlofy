package com.hyperlofy.backend.security.controller;

import com.hyperlofy.backend.security.entity.ComplianceControl;
import com.hyperlofy.backend.security.entity.SecurityPolicy;
import com.hyperlofy.backend.security.service.EnterpriseSecurityGovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security/admin")
@RequiredArgsConstructor
@Tag(name = "Enterprise Security & Governance Admin API", description = "Endpoints for Principal Security & GRC Architects to inspect security policies and compliance control status")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class SecurityAdminController {

    private final EnterpriseSecurityGovernanceService securityService;

    @GetMapping("/policies")
    @Operation(summary = "Get Registered Security Policies", description = "Returns active Zero Trust policies, ABAC rules, and data masking specifications.")
    public ResponseEntity<List<SecurityPolicy>> getPolicies() {
        return ResponseEntity.ok(securityService.getAllPolicies());
    }

    @GetMapping("/compliance")
    @Operation(summary = "Get Compliance Controls Posture", description = "Returns posture summary across SOC2, ISO 27001, GDPR, and PCI-DSS compliance frameworks.")
    public ResponseEntity<List<ComplianceControl>> getCompliance() {
        return ResponseEntity.ok(securityService.getAllComplianceControls());
    }
}
