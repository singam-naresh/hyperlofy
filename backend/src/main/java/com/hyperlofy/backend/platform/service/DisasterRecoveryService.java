package com.hyperlofy.backend.platform.service;

import com.hyperlofy.backend.platform.entity.DrFailoverLog;
import com.hyperlofy.backend.platform.entity.DrRecoveryMetric;
import com.hyperlofy.backend.platform.entity.DrRunbook;
import com.hyperlofy.backend.platform.entity.IncidentRecord;
import com.hyperlofy.backend.platform.repository.DrFailoverLogRepository;
import com.hyperlofy.backend.platform.repository.DrRecoveryMetricRepository;
import com.hyperlofy.backend.platform.repository.DrRunbookRepository;
import com.hyperlofy.backend.platform.repository.IncidentRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisasterRecoveryService {

    private static final Logger log = LoggerFactory.getLogger(DisasterRecoveryService.class);

    private final DrRecoveryMetricRepository metricRepository;
    private final DrFailoverLogRepository failoverLogRepository;
    private final IncidentRecordRepository incidentRepository;
    private final DrRunbookRepository runbookRepository;

    @Transactional
    public DrFailoverLog executeFailover(String targetSystem, String oldNode, String newNode, String reason, String actor, boolean isAutomated) {
        log.warn("[DISASTER RECOVERY ALERT] Executing failover for system: {}, oldNode={}, newNode={}", targetSystem, oldNode, newNode);
        DrFailoverLog logEntry = DrFailoverLog.builder()
                .targetSystem(targetSystem)
                .oldActiveNode(oldNode)
                .newActiveNode(newNode)
                .failoverReason(reason)
                .initiatedBy(actor)
                .isAutomated(isAutomated)
                .build();
        return failoverLogRepository.save(logEntry);
    }

    @Transactional
    public IncidentRecord reportIncident(String title, String severity, String rootCause) {
        String code = "INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.error("[INCIDENT REPORTED] Code={}, Severity={}, Title={}", code, severity, title);
        IncidentRecord record = IncidentRecord.builder()
                .incidentCode(code)
                .severity(severity)
                .title(title)
                .rootCause(rootCause)
                .detectedAt(ZonedDateTime.now())
                .build();
        return incidentRepository.save(record);
    }

    @Transactional(readOnly = true)
    public DrRecoveryMetric getServiceRecoveryTarget(String serviceName) {
        return metricRepository.findByServiceName(serviceName).orElseGet(() ->
                DrRecoveryMetric.builder()
                        .serviceName(serviceName)
                        .targetRtoSeconds(300)
                        .targetRpoSeconds(0)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<DrRunbook> getAllRunbooks() {
        return runbookRepository.findAll();
    }
}
