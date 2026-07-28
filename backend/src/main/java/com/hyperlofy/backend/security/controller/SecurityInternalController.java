package com.hyperlofy.backend.security.controller;

import com.hyperlofy.backend.security.entity.ComplianceControl;
import com.hyperlofy.backend.security.entity.PrivilegedSession;
import com.hyperlofy.backend.security.entity.RiskRegister;
import com.hyperlofy.backend.security.entity.SecurityPolicy;
import com.hyperlofy.backend.security.service.EnterpriseSecurityGovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/security/internal")
@RequiredArgsConstructor
@Tag(name = "Enterprise Security & Governance Internal API", description = "Endpoints for Zero Trust policy enforcement, JIT privileged access requests, risk logging, and automated compliance testing")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SecurityInternalController {

    private final EnterpriseSecurityGovernanceService securityService;

    @PostMapping("/policies")
    @Operation(summary = "Register Security Policy (ABAC/PBAC/Zero-Trust)", description = "Registers Zero Trust ABAC/PBAC policy rule expression for Policy Enforcement Points (PEP).")
    public ResponseEntity<SecurityPolicy> createPolicy(
            @RequestParam String policyCode,
            @RequestParam String policyName,
            @RequestParam String policyType,
            @RequestParam(required = false) String effect,
            @RequestBody String ruleExpression) {
        return ResponseEntity.ok(securityService.createPolicy(policyCode, policyName, policyType, effect, ruleExpression));
    }

    @PostMapping("/jit-access")
    @Operation(summary = "Grant Just-In-Time Privileged Access Session", description = "Grants temporary elevated privilege session with explicit audit justification and expiration time.")
    public ResponseEntity<PrivilegedSession> grantJitAccess(
            @RequestParam UUID userId,
            @RequestParam String requestedRole,
            @RequestParam String justification,
            @RequestParam(required = false) Integer durationMinutes) {
        return ResponseEntity.ok(securityService.grantPrivilegedSession(userId, requestedRole, justification, durationMinutes));
    }

    @PostMapping("/risk")
    @Operation(summary = "Log Enterprise Risk & Threat Indicator", description = "Registers cybersecurity or operational risk entry into the central enterprise risk register.")
    public ResponseEntity<RiskRegister> logRisk(
            @RequestParam String riskCode,
            @RequestParam String riskTitle,
            @RequestParam String category,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) BigDecimal impactScore) {
        return ResponseEntity.ok(securityService.logRisk(riskCode, riskTitle, category, severity, impactScore));
    }

    @PostMapping("/compliance")
    @Operation(summary = "Record Compliance Control Evidence Test", description = "Records automated compliance control validation (SOC2, ISO 27001, GDPR, PCI-DSS) and audit evidence.")
    public ResponseEntity<ComplianceControl> recordCompliance(
            @RequestParam String controlCode,
            @RequestParam String framework,
            @RequestParam String controlName,
            @RequestParam(required = false) String evidenceUrl,
            @RequestParam boolean passed) {
        return ResponseEntity.ok(securityService.recordComplianceTest(controlCode, framework, controlName, evidenceUrl, passed));
    }
}
