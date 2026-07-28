package com.hyperlofy.backend.admin.service;

import com.hyperlofy.backend.admin.entity.AdminAction;
import com.hyperlofy.backend.admin.entity.AdminCase;
import com.hyperlofy.backend.admin.entity.AdminFeatureFlag;
import com.hyperlofy.backend.admin.entity.AdminIncident;
import com.hyperlofy.backend.admin.repository.AdminActionRepository;
import com.hyperlofy.backend.admin.repository.AdminCaseRepository;
import com.hyperlofy.backend.admin.repository.AdminFeatureFlagRepository;
import com.hyperlofy.backend.admin.repository.AdminIncidentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminOperationsService {

    private static final Logger log = LoggerFactory.getLogger(AdminOperationsService.class);

    private final AdminCaseRepository caseRepository;
    private final AdminIncidentRepository incidentRepository;
    private final AdminFeatureFlagRepository flagRepository;
    private final AdminActionRepository actionRepository;

    @Transactional
    public AdminAction logAdminAction(String adminUser, String actionType, UUID targetId, String description) {
        log.info("[ADMIN PLATFORM] Logging operational audit action: Admin={}, Action={}, Target={}", adminUser, actionType, targetId);

        AdminAction action = AdminAction.builder()
                .adminUser(adminUser)
                .actionType(actionType)
                .targetId(targetId)
                .description(description)
                .build();

        return actionRepository.save(action);
    }

    @Transactional
    public AdminCase createSupportCase(String subject, UUID customerId, UUID orderId, String priority) {
        log.info("[ADMIN PLATFORM] Opening support case: Subject={}, CustomerId={}, OrderId={}", subject, customerId, orderId);

        String caseNo = "CASE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        AdminCase adminCase = AdminCase.builder()
                .caseNumber(caseNo)
                .subject(subject)
                .customerId(customerId)
                .orderId(orderId)
                .priority(priority != null ? priority : "MEDIUM")
                .status("OPEN")
                .assignedTo("TIER_1_SUPPORT")
                .build();

        return caseRepository.save(adminCase);
    }

    @Transactional
    public AdminIncident reportIncident(String title, String incidentType, String severity, String reportedBy) {
        log.info("[ADMIN PLATFORM] Reporting operational incident: Title={}, Severity={}, Reporter={}", title, severity, reportedBy);

        String incNo = "INC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        AdminIncident incident = AdminIncident.builder()
                .incidentNumber(incNo)
                .title(title)
                .incidentType(incidentType)
                .severity(severity)
                .status("ACTIVE")
                .reportedBy(reportedBy)
                .build();

        return incidentRepository.save(incident);
    }

    @Transactional
    public AdminFeatureFlag toggleFeatureFlag(String flagKey, Boolean isEnabled, Integer rolloutPercentage) {
        log.info("[ADMIN PLATFORM] Updating system feature flag: Key={}, Enabled={}", flagKey, isEnabled);

        AdminFeatureFlag flag = flagRepository.findByFlagKey(flagKey).orElseGet(() ->
                AdminFeatureFlag.builder()
                        .flagKey(flagKey)
                        .flagName("Dynamic Feature Flag " + flagKey)
                        .build()
        );

        if (isEnabled != null) flag.setIsEnabled(isEnabled);
        if (rolloutPercentage != null) flag.setRolloutPercentage(rolloutPercentage);

        return flagRepository.save(flag);
    }

    @Transactional(readOnly = true)
    public List<AdminIncident> getActiveIncidents() {
        return incidentRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
    }
}
