package com.hyperlofy.backend.eip.controller;

import com.hyperlofy.backend.eip.entity.IntegrationConnector;
import com.hyperlofy.backend.eip.entity.IntegrationFailure;
import com.hyperlofy.backend.eip.service.EnterpriseIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integration/admin")
@RequiredArgsConstructor
@Tag(name = "Enterprise Integration Platform Admin API", description = "Endpoints for Principal Integration Architects to monitor B2B connectors and replay DLQ failure events")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class IntegrationAdminController {

    private final EnterpriseIntegrationService integrationService;

    @GetMapping("/connectors")
    @Operation(summary = "Get Registered Enterprise Connectors", description = "Returns active ERP, CRM, Accounting, and Logistics connectors and health statuses.")
    public ResponseEntity<List<IntegrationConnector>> getConnectors() {
        return ResponseEntity.ok(integrationService.getAllConnectors());
    }

    @PostMapping("/replay")
    @Operation(summary = "Replay DLQ Failed Integration Event", description = "Replays failed inbound/outbound event from Dead Letter Queue following circuit breaker recovery.")
    public ResponseEntity<IntegrationFailure> replayFailure(@RequestParam UUID failureId) {
        return ResponseEntity.ok(integrationService.replayFailedEvent(failureId));
    }
}
