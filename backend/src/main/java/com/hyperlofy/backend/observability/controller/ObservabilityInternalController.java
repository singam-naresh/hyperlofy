package com.hyperlofy.backend.observability.controller;

import com.hyperlofy.backend.observability.entity.DistributedTrace;
import com.hyperlofy.backend.observability.entity.RunbookExecution;
import com.hyperlofy.backend.observability.entity.TelemetryEvent;
import com.hyperlofy.backend.observability.service.EnterpriseObservabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/observability/internal")
@RequiredArgsConstructor
@Tag(name = "Enterprise Observability Internal API", description = "Endpoints for OpenTelemetry ingestion, distributed tracing correlation, and autonomous self-healing runbook execution")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ObservabilityInternalController {

    private final EnterpriseObservabilityService observabilityService;

    @PostMapping("/events")
    @Operation(summary = "Ingest Unified Telemetry Event", description = "Ingests structured logs, metrics, or synthetic monitoring events correlated via OpenTelemetry B3/W3C context.")
    public ResponseEntity<TelemetryEvent> ingestEvent(
            @RequestParam String serviceName,
            @RequestParam String eventType,
            @RequestParam String metricName,
            @RequestParam BigDecimal metricValue,
            @RequestParam String correlationId) {
        return ResponseEntity.ok(observabilityService.ingestTelemetryEvent(serviceName, eventType, metricName, metricValue, correlationId));
    }

    @PostMapping("/traces")
    @Operation(summary = "Record OpenTelemetry Distributed Span", description = "Records distributed tracing span details (spanId, parentSpanId, duration) for Jaeger/Tempo visualization.")
    public ResponseEntity<DistributedTrace> recordTrace(
            @RequestParam String traceId,
            @RequestParam String spanId,
            @RequestParam(required = false) String parentSpanId,
            @RequestParam String serviceName,
            @RequestParam String operationName,
            @RequestParam(required = false) Long durationMs,
            @RequestParam(required = false) String statusCode) {
        return ResponseEntity.ok(observabilityService.recordSpan(traceId, spanId, parentSpanId, serviceName, operationName, durationMs, statusCode));
    }

    @PostMapping("/runbook")
    @Operation(summary = "Trigger Autonomous Self-Healing Runbook", description = "Executes automated remediation action (pod restart, traffic shift, capacity scaling) upon AIOps anomaly trigger.")
    public ResponseEntity<RunbookExecution> executeRunbook(
            @RequestParam String runbookName,
            @RequestParam String targetService,
            @RequestParam String actionType) {
        return ResponseEntity.ok(observabilityService.executeAutonomousRunbook(runbookName, targetService, actionType));
    }
}
