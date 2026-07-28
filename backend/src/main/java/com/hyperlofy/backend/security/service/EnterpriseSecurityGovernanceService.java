package com.hyperlofy.backend.security.service;

import com.hyperlofy.backend.security.entity.ComplianceControl;
import com.hyperlofy.backend.security.entity.PrivilegedSession;
import com.hyperlofy.backend.security.entity.RiskRegister;
import com.hyperlofy.backend.security.entity.SecurityPolicy;
import com.hyperlofy.backend.security.repository.ComplianceControlRepository;
import com.hyperlofy.backend.security.repository.PrivilegedSessionRepository;
import com.hyperlofy.backend.security.repository.RiskRegisterRepository;
import com.hyperlofy.backend.security.repository.SecurityPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnterpriseSecurityGovernanceService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseSecurityGovernanceService.class);

    private final SecurityPolicyRepository policyRepository;
    private final PrivilegedSessionRepository sessionRepository;
    private final RiskRegisterRepository riskRepository;
    private final ComplianceControlRepository complianceRepository;

    @Transactional
    public SecurityPolicy createPolicy(String policyCode, String policyName, String policyType, String effect, String ruleExpression) {
        log.info("[ENTERPRISE SECURITY] Creating security policy Code={}, Name={}, Type={}, Effect={}", policyCode, policyName, policyType, effect);

        SecurityPolicy policy = policyRepository.findByPolicyCode(policyCode).orElseGet(() ->
                SecurityPolicy.builder()
                        .policyCode(policyCode)
                        .policyName(policyName)
                        .policyType(policyType)
                        .effect(effect != null ? effect : "ALLOW")
                        .ruleExpression(ruleExpression)
                        .status("ACTIVE")
                        .build()
        );

        return policyRepository.save(policy);
    }

    @Transactional
    public PrivilegedSession grantPrivilegedSession(UUID userId, String requestedRole, String justification, Integer durationMinutes) {
        log.info("[ENTERPRISE SECURITY] Granting JIT Privileged Access UserId={}, Role={}, Justification={}", userId, requestedRole, justification);

        int duration = (durationMinutes != null && durationMinutes > 0) ? durationMinutes : 60;

        PrivilegedSession session = PrivilegedSession.builder()
                .userId(userId)
                .requestedRole(requestedRole)
                .justification(justification)
                .sessionStatus("ACTIVE")
                .riskScore(new BigDecimal("10.00"))
                .startedAt(OffsetDateTime.now())
                .expiresAt(OffsetDateTime.now().plusMinutes(duration))
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public RiskRegister logRisk(String riskCode, String riskTitle, String category, String severity, BigDecimal impactScore) {
        log.info("[ENTERPRISE SECURITY] Registering enterprise security risk Code={}, Title={}, Category={}, Severity={}",
                riskCode, riskTitle, category, severity);

        RiskRegister risk = riskRepository.findByRiskCode(riskCode).orElseGet(() ->
                RiskRegister.builder()
                        .riskCode(riskCode)
                        .riskTitle(riskTitle)
                        .category(category)
                        .severity(severity != null ? severity : "HIGH")
                        .impactScore(impactScore != null ? impactScore : new BigDecimal("85.00"))
                        .status("IDENTIFIED")
                        .build()
        );

        return riskRepository.save(risk);
    }

    @Transactional
    public ComplianceControl recordComplianceTest(String controlCode, String framework, String controlName, String evidenceUrl, boolean passed) {
        log.info("[ENTERPRISE SECURITY] Recording compliance control test Control={}, Framework={}, Passed={}", controlCode, framework, passed);

        ComplianceControl control = complianceRepository.findByControlCode(controlCode).orElseGet(() ->
                ComplianceControl.builder()
                        .controlCode(controlCode)
                        .framework(framework)
                        .controlName(controlName)
                        .evidenceUrl(evidenceUrl)
                        .status(passed ? "PASSED" : "FAILED")
                        .lastTestedAt(OffsetDateTime.now())
                        .build()
        );

        control.setStatus(passed ? "PASSED" : "FAILED");
        control.setLastTestedAt(OffsetDateTime.now());
        if (evidenceUrl != null) control.setEvidenceUrl(evidenceUrl);

        return complianceRepository.save(control);
    }

    @Transactional(readOnly = true)
    public List<SecurityPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ComplianceControl> getAllComplianceControls() {
        return complianceRepository.findAll();
    }
}
