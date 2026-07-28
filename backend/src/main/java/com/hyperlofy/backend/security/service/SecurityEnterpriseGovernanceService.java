package com.hyperlofy.backend.security.service;

import com.hyperlofy.backend.security.entity.DataSubjectRequest;
import com.hyperlofy.backend.security.entity.IdentityLifecycle;
import com.hyperlofy.backend.security.entity.PrivacyConsent;
import com.hyperlofy.backend.security.entity.SecurityPlaybook;
import com.hyperlofy.backend.security.repository.DataSubjectRequestRepository;
import com.hyperlofy.backend.security.repository.IdentityLifecycleRepository;
import com.hyperlofy.backend.security.repository.PrivacyConsentRepository;
import com.hyperlofy.backend.security.repository.SecurityPlaybookRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SecurityEnterpriseGovernanceService {

    private static final Logger log = LoggerFactory.getLogger(SecurityEnterpriseGovernanceService.class);

    private final IdentityLifecycleRepository lifecycleRepository;
    private final PrivacyConsentRepository consentRepository;
    private final DataSubjectRequestRepository dsarRepository;
    private final SecurityPlaybookRepository playbookRepository;

    @Transactional
    public IdentityLifecycle triggerLifecycleWorkflow(UUID userId, String workflowType, String birthrightRole) {
        log.info("[SECURITY ENTERPRISE] Triggering Identity Lifecycle Workflow UserId={}, Type={}, BirthrightRole={}",
                userId, workflowType, birthrightRole);

        IdentityLifecycle lifecycle = IdentityLifecycle.builder()
                .userId(userId)
                .workflowType(workflowType)
                .birthrightRole(birthrightRole)
                .status("COMPLETED")
                .build();

        return lifecycleRepository.save(lifecycle);
    }

    @Transactional
    public PrivacyConsent recordConsent(UUID userId, String consentType, boolean granted, String ipAddress) {
        log.info("[SECURITY ENTERPRISE] Recording GDPR Privacy Consent UserId={}, Type={}, Granted={}", userId, consentType, granted);

        PrivacyConsent consent = PrivacyConsent.builder()
                .userId(userId)
                .consentType(consentType)
                .granted(granted)
                .ipAddress(ipAddress)
                .grantedAt(OffsetDateTime.now())
                .build();

        return consentRepository.save(consent);
    }

    @Transactional
    public DataSubjectRequest submitDsarRequest(UUID userId, String requestType) {
        log.info("[SECURITY ENTERPRISE] Submitting GDPR/CCPA Data Subject Request (DSAR) UserId={}, Type={}", userId, requestType);

        DataSubjectRequest request = DataSubjectRequest.builder()
                .userId(userId)
                .requestType(requestType)
                .status("PROCESSED")
                .processedAt(OffsetDateTime.now())
                .build();

        return dsarRepository.save(request);
    }

    @Transactional
    public SecurityPlaybook executeSoarPlaybook(String playbookCode, String playbookName, String triggerEvent, String automatedAction) {
        log.info("[SECURITY ENTERPRISE] Executing SOAR Security Playbook Code={}, Event={}, Action={}", playbookCode, triggerEvent, automatedAction);

        SecurityPlaybook playbook = playbookRepository.findByPlaybookCode(playbookCode).orElseGet(() ->
                SecurityPlaybook.builder()
                        .playbookCode(playbookCode)
                        .playbookName(playbookName)
                        .triggerEvent(triggerEvent)
                        .automatedAction(automatedAction)
                        .executionStatus("EXECUTED")
                        .build()
        );

        return playbookRepository.save(playbook);
    }

    @Transactional(readOnly = true)
    public List<SecurityPlaybook> getActivePlaybooks() {
        return playbookRepository.findAll();
    }
}
