package com.hyperlofy.backend.observability.controller;

import com.hyperlofy.backend.observability.entity.AnomalyReport;
import com.hyperlofy.backend.observability.entity.DistributedTrace;
import com.hyperlofy.backend.observability.service.EnterpriseObservabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/observability/admin")
@RequiredArgsConstructor
@Tag(name = "Enterprise Observability Admin API", description = "Endpoints for Principal Observability & SRE Architects to query distributed traces and AIOps anomaly reports")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class ObservabilityAdminController {

    private final EnterpriseObservabilityService observabilityService;

    @GetMapping("/traces")
    @Operation(summary = "Get Distributed Spans by Trace ID", description = "Returns full span execution tree across microservice boundaries for specified distributed trace ID.")
    public ResponseEntity<List<DistributedTrace>> getTraces(@RequestParam String traceId) {
        return ResponseEntity.ok(observabilityService.getTracesByTraceId(traceId));
    }

    @GetMapping("/incidents")
    @Operation(summary = "Get AIOps Anomaly Reports by Service", description = "Returns active AIOps latency spikes, error bursts, and memory leak reports for specified service.")
    public ResponseEntity<List<AnomalyReport>> getAnomalies(@RequestParam String serviceName) {
        return ResponseEntity.ok(observabilityService.getAnomaliesByService(serviceName));
    }
}
