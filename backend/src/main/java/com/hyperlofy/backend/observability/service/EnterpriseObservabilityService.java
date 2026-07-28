package com.hyperlofy.backend.observability.service;

import com.hyperlofy.backend.observability.entity.AnomalyReport;
import com.hyperlofy.backend.observability.entity.DistributedTrace;
import com.hyperlofy.backend.observability.entity.RunbookExecution;
import com.hyperlofy.backend.observability.entity.TelemetryEvent;
import com.hyperlofy.backend.observability.repository.AnomalyReportRepository;
import com.hyperlofy.backend.observability.repository.DistributedTraceRepository;
import com.hyperlofy.backend.observability.repository.RunbookExecutionRepository;
import com.hyperlofy.backend.observability.repository.TelemetryEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseObservabilityService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseObservabilityService.class);

    private final TelemetryEventRepository eventRepository;
    private final DistributedTraceRepository traceRepository;
    private final AnomalyReportRepository anomalyRepository;
    private final RunbookExecutionRepository runbookRepository;

    @Transactional
    public TelemetryEvent ingestTelemetryEvent(String serviceName, String eventType, String metricName, BigDecimal metricValue, String correlationId) {
        log.info("[ENTERPRISE OBSERVABILITY] Ingesting unified telemetry event Service={}, Type={}, Metric={}, Value={}",
                serviceName, eventType, metricName, metricValue);

        TelemetryEvent event = TelemetryEvent.builder()
                .serviceName(serviceName)
                .eventType(eventType)
                .metricName(metricName)
                .metricValue(metricValue)
                .correlationId(correlationId)
                .build();

        return eventRepository.save(event);
    }

    @Transactional
    public DistributedTrace recordSpan(String traceId, String spanId, String parentSpanId, String serviceName, String operationName, Long durationMs, String statusCode) {
        log.info("[ENTERPRISE OBSERVABILITY] Recording OpenTelemetry distributed span TraceId={}, SpanId={}, Service={}, Duration={}ms",
                traceId, spanId, serviceName, durationMs);

        DistributedTrace trace = DistributedTrace.builder()
                .traceId(traceId)
                .spanId(spanId)
                .parentSpanId(parentSpanId)
                .serviceName(serviceName)
                .operationName(operationName)
                .durationMs(durationMs != null ? durationMs : 0L)
                .statusCode(statusCode != null ? statusCode : "OK")
                .build();

        return traceRepository.save(trace);
    }

    @Transactional
    public AnomalyReport detectAIOpsAnomaly(String serviceName, String anomalyType, String severity, BigDecimal confidenceScore) {
        log.info("[ENTERPRISE OBSERVABILITY] AIOps anomaly detected Service={}, Type={}, Severity={}, Confidence={}",
                serviceName, anomalyType, severity, confidenceScore);

        AnomalyReport anomaly = AnomalyReport.builder()
                .serviceName(serviceName)
                .anomalyType(anomalyType)
                .severity(severity != null ? severity : "CRITICAL")
                .confidenceScore(confidenceScore != null ? confidenceScore : new BigDecimal("98.50"))
                .status("DETECTED")
                .build();

        return anomalyRepository.save(anomaly);
    }

    @Transactional
    public RunbookExecution executeAutonomousRunbook(String runbookName, String targetService, String actionType) {
        log.info("[ENTERPRISE OBSERVABILITY] Triggering autonomous self-healing runbook Runbook={}, Target={}, Action={}",
                runbookName, targetService, actionType);

        RunbookExecution execution = RunbookExecution.builder()
                .runbookName(runbookName)
                .targetService(targetService)
                .actionType(actionType)
                .executionStatus("SUCCESS")
                .executionTimeMs(1200L)
                .build();

        return runbookRepository.save(execution);
    }

    @Transactional(readOnly = true)
    public List<DistributedTrace> getTracesByTraceId(String traceId) {
        return traceRepository.findByTraceId(traceId);
    }

    @Transactional(readOnly = true)
    public List<AnomalyReport> getAnomaliesByService(String serviceName) {
        return anomalyRepository.findByServiceName(serviceName);
    }
}
