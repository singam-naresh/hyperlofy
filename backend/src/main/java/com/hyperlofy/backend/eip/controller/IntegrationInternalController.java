package com.hyperlofy.backend.eip.controller;

import com.hyperlofy.backend.eip.entity.IntegrationConnector;
import com.hyperlofy.backend.eip.entity.IntegrationJob;
import com.hyperlofy.backend.eip.entity.IntegrationWebhook;
import com.hyperlofy.backend.eip.service.EnterpriseIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integration/internal")
@RequiredArgsConstructor
@Tag(name = "Enterprise Integration Platform Internal API", description = "Endpoints for registering B2B connectors (SAP, Salesforce, Tally, FedEx), triggering sync jobs, and ingesting webhooks")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class IntegrationInternalController {

    private final EnterpriseIntegrationService integrationService;

    @PostMapping("/connectors")
    @Operation(summary = "Register B2B Enterprise Connector", description = "Registers enterprise connector for ERP (SAP, NetSuite), CRM (Salesforce), Accounting (Tally), or Logistics (FedEx).")
    public ResponseEntity<IntegrationConnector> registerConnector(
            @RequestParam String connectorCode,
            @RequestParam String connectorName,
            @RequestParam String systemType,
            @RequestParam String providerName,
            @RequestParam String endpointUrl) {
        return ResponseEntity.ok(integrationService.registerConnector(connectorCode, connectorName, systemType, providerName, endpointUrl));
    }

    @PostMapping("/sync")
    @Operation(summary = "Execute Enterprise Data Sync Job", description = "Triggers bi-directional data sync pipeline for inventory, purchase orders, or invoice ledgers.")
    public ResponseEntity<IntegrationJob> executeSync(
            @RequestParam UUID connectorId,
            @RequestParam String jobType,
            @RequestParam(required = false) Integer recordsProcessed) {
        return ResponseEntity.ok(integrationService.executeSyncJob(connectorId, jobType, recordsProcessed));
    }

    @PostMapping("/webhooks")
    @Operation(summary = "Ingest Inbound B2B Partner Webhook", description = "Receives and validates signed inbound webhook payload from external ERP, CRM, or shipping carriers.")
    public ResponseEntity<IntegrationWebhook> processWebhook(
            @RequestParam UUID connectorId,
            @RequestParam String eventType,
            @RequestParam String payloadHash) {
        return ResponseEntity.ok(integrationService.processInboundWebhook(connectorId, eventType, payloadHash));
    }
}
