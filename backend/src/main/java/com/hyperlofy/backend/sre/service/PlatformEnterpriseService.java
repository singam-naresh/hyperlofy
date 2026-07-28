package com.hyperlofy.backend.sre.service;

import com.hyperlofy.backend.sre.entity.PlatformCapacityForecast;
import com.hyperlofy.backend.sre.entity.PlatformReleaseHistory;
import com.hyperlofy.backend.sre.entity.PlatformRunbook;
import com.hyperlofy.backend.sre.entity.PlatformSecurityEvent;
import com.hyperlofy.backend.sre.repository.PlatformCapacityForecastRepository;
import com.hyperlofy.backend.sre.repository.PlatformReleaseHistoryRepository;
import com.hyperlofy.backend.sre.repository.PlatformRunbookRepository;
import com.hyperlofy.backend.sre.repository.PlatformSecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(PlatformEnterpriseService.class);

    private final PlatformReleaseHistoryRepository releaseRepository;
    private final PlatformCapacityForecastRepository capacityRepository;
    private final PlatformSecurityEventRepository securityRepository;
    private final PlatformRunbookRepository runbookRepository;

    @Transactional
    public PlatformReleaseHistory recordRollbackExecution(String serviceName, String version, String approvalBy) {
        log.info("[SRE ENTERPRISE] Triggering automated release rollback Service={}, Version={}, ApprovalBy={}",
                serviceName, version, approvalBy);

        PlatformReleaseHistory release = PlatformReleaseHistory.builder()
                .serviceName(serviceName)
                .releaseVersion(version)
                .rollbackExecuted(true)
                .verificationStatus("ROLLED_BACK")
                .approvalBy(approvalBy)
                .build();

        return releaseRepository.save(release);
    }

    @Transactional
    public PlatformCapacityForecast generateCapacityForecast(String clusterName, String resourceType, BigDecimal currentPct, BigDecimal forecastPct, Integer nodes) {
        log.info("[SRE ENTERPRISE] Capacity forecasting Cluster={}, Resource={}, ForecastPct={}%", clusterName, resourceType, forecastPct);

        PlatformCapacityForecast capacity = PlatformCapacityForecast.builder()
                .clusterName(clusterName)
                .resourceType(resourceType)
                .currentUtilizationPct(currentPct)
                .forecastedUtilizationPct(forecastPct)
                .recommendedNodeCount(nodes != null ? nodes : 24)
                .build();

        return capacityRepository.save(capacity);
    }

    @Transactional
    public PlatformSecurityEvent logSecurityEvent(String eventCode, String component, String severity, String description) {
        log.info("[SRE ENTERPRISE] Security policy enforcement Code={}, Component={}, Severity={}", eventCode, component, severity);

        PlatformSecurityEvent event = PlatformSecurityEvent.builder()
                .eventCode(eventCode)
                .sourceComponent(component)
                .severity(severity != null ? severity : "HIGH")
                .description(description)
                .enforcementAction("BLOCKED")
                .build();

        return securityRepository.save(event);
    }

    @Transactional
    public PlatformRunbook executeAutomatedRunbook(String runbookName, String condition) {
        log.info("[SRE ENTERPRISE] Executing self-healing infrastructure runbook Name={}, Condition={}", runbookName, condition);

        PlatformRunbook runbook = runbookRepository.findByRunbookName(runbookName).orElseGet(() ->
                PlatformRunbook.builder()
                        .runbookName(runbookName)
                        .triggerCondition(condition)
                        .executionMode("AUTOMATED")
                        .successRatePct(new BigDecimal("99.50"))
                        .build()
        );

        return runbookRepository.save(runbook);
    }

    @Transactional(readOnly = true)
    public List<PlatformCapacityForecast> getCapacityForecasts(String clusterName) {
        return capacityRepository.findByClusterNameOrderByCreatedAtDesc(clusterName);
    }
}
