package com.hyperlofy.backend.security.controller;

import com.hyperlofy.backend.security.entity.DataSubjectRequest;
import com.hyperlofy.backend.security.entity.IdentityLifecycle;
import com.hyperlofy.backend.security.entity.PrivacyConsent;
import com.hyperlofy.backend.security.entity.SecurityPlaybook;
import com.hyperlofy.backend.security.service.SecurityEnterpriseGovernanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/security/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Security Platform Enterprise Addendum API", description = "Endpoints for Identity Lifecycle JML workflows, GDPR Privacy Consent & DSAR requests, and SOAR automated incident response playbooks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SecurityEnterpriseController {

    private final SecurityEnterpriseGovernanceService enterpriseService;

    @PostMapping("/identity")
    @Operation(summary = "Trigger Identity Lifecycle Workflow (JML)", description = "Triggers Joiner-Mover-Leaver automated identity lifecycle workflow and birthright role assignments.")
    public ResponseEntity<IdentityLifecycle> triggerLifecycle(
            @RequestParam UUID userId,
            @RequestParam String workflowType,
            @RequestParam String birthrightRole) {
        return ResponseEntity.ok(enterpriseService.triggerLifecycleWorkflow(userId, workflowType, birthrightRole));
    }

    @PostMapping("/privacy/consent")
    @Operation(summary = "Record GDPR / CCPA Privacy Consent", description = "Records explicit user privacy consent for marketing, analytics, or third-party data sharing.")
    public ResponseEntity<PrivacyConsent> recordConsent(
            @RequestParam UUID userId,
            @RequestParam String consentType,
            @RequestParam boolean granted,
            @RequestParam(required = false) String ipAddress) {
        return ResponseEntity.ok(enterpriseService.recordConsent(userId, consentType, granted, ipAddress));
    }

    @PostMapping("/privacy/dsar")
    @Operation(summary = "Submit GDPR Data Subject Access/Erasure Request", description = "Submits Data Subject Access Request (DSAR) for Right to Access, Erasure, or Rectification.")
    public ResponseEntity<DataSubjectRequest> submitDsar(
            @RequestParam UUID userId,
            @RequestParam String requestType) {
        return ResponseEntity.ok(enterpriseService.submitDsarRequest(userId, requestType));
    }

    @PostMapping("/playbooks")
    @Operation(summary = "Execute SOAR Incident Response Playbook", description = "Triggers automated Security Orchestration, Automation, and Response (SOAR) playbooks for threat mitigation.")
    public ResponseEntity<SecurityPlaybook> executePlaybook(
            @RequestParam String playbookCode,
            @RequestParam String playbookName,
            @RequestParam String triggerEvent,
            @RequestParam String automatedAction) {
        return ResponseEntity.ok(enterpriseService.executeSoarPlaybook(playbookCode, playbookName, triggerEvent, automatedAction));
    }

    @GetMapping("/playbooks")
    @Operation(summary = "Get Active SOAR Playbooks", description = "Lists automated incident response playbooks configured in the SOAR engine.")
    public ResponseEntity<List<SecurityPlaybook>> getPlaybooks() {
        return ResponseEntity.ok(enterpriseService.getActivePlaybooks());
    }
}
