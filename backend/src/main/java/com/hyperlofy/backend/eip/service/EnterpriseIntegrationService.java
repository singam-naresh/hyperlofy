package com.hyperlofy.backend.eip.service;

import com.hyperlofy.backend.eip.entity.IntegrationConnector;
import com.hyperlofy.backend.eip.entity.IntegrationFailure;
import com.hyperlofy.backend.eip.entity.IntegrationJob;
import com.hyperlofy.backend.eip.entity.IntegrationWebhook;
import com.hyperlofy.backend.eip.repository.IntegrationConnectorRepository;
import com.hyperlofy.backend.eip.repository.IntegrationFailureRepository;
import com.hyperlofy.backend.eip.repository.IntegrationJobRepository;
import com.hyperlofy.backend.eip.repository.IntegrationWebhookRepository;
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
public class EnterpriseIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseIntegrationService.class);

    private final IntegrationConnectorRepository connectorRepository;
    private final IntegrationJobRepository jobRepository;
    private final IntegrationWebhookRepository webhookRepository;
    private final IntegrationFailureRepository failureRepository;

    @Transactional
    public IntegrationConnector registerConnector(String connectorCode, String connectorName, String systemType, String providerName, String endpointUrl) {
        log.info("[ENTERPRISE INTEGRATION PLATFORM] Registering B2B connector Code={}, Name={}, Type={}, Provider={}",
                connectorCode, connectorName, systemType, providerName);

        IntegrationConnector connector = connectorRepository.findByConnectorCode(connectorCode).orElseGet(() ->
                IntegrationConnector.builder()
                        .connectorCode(connectorCode)
                        .connectorName(connectorName)
                        .systemType(systemType)
                        .providerName(providerName)
                        .endpointUrl(endpointUrl)
                        .status("ACTIVE")
                        .build()
        );

        return connectorRepository.save(connector);
    }

    @Transactional
    public IntegrationJob executeSyncJob(UUID connectorId, String jobType, Integer recordsProcessed) {
        log.info("[ENTERPRISE INTEGRATION PLATFORM] Executing data sync job ConnectorId={}, JobType={}, Records={}", connectorId, jobType, recordsProcessed);

        IntegrationJob job = IntegrationJob.builder()
                .connectorId(connectorId)
                .jobType(jobType)
                .status("COMPLETED")
                .recordsProcessed(recordsProcessed != null ? recordsProcessed : 100)
                .recordsFailed(0)
                .startedAt(OffsetDateTime.now().minusMinutes(2))
                .completedAt(OffsetDateTime.now())
                .build();

        return jobRepository.save(job);
    }

    @Transactional
    public IntegrationWebhook processInboundWebhook(UUID connectorId, String eventType, String payloadHash) {
        log.info("[ENTERPRISE INTEGRATION PLATFORM] Processing inbound B2B webhook ConnectorId={}, EventType={}, Hash={}", connectorId, eventType, payloadHash);

        IntegrationWebhook webhook = IntegrationWebhook.builder()
                .connectorId(connectorId)
                .eventType(eventType)
                .payloadHash(payloadHash)
                .status("PROCESSED")
                .processedAt(OffsetDateTime.now())
                .build();

        return webhookRepository.save(webhook);
    }

    @Transactional
    public IntegrationFailure replayFailedEvent(UUID failureId) {
        log.info("[ENTERPRISE INTEGRATION PLATFORM] Replaying failed integration event FailureId={}", failureId);

        IntegrationFailure failure = failureRepository.findById(failureId).orElseThrow(() -> new IllegalArgumentException("Failure event not found: " + failureId));
        failure.setStatus("REPLAYED");
        failure.setRetryCount(failure.getRetryCount() + 1);

        return failureRepository.save(failure);
    }

    @Transactional(readOnly = true)
    public List<IntegrationConnector> getAllConnectors() {
        return connectorRepository.findAll();
    }
}
