package com.hyperlofy.backend.eip.controller;

import com.hyperlofy.backend.eip.entity.B2bMessage;
import com.hyperlofy.backend.eip.entity.CdcStream;
import com.hyperlofy.backend.eip.entity.ConnectorMarketplace;
import com.hyperlofy.backend.eip.entity.MasterDataRegistry;
import com.hyperlofy.backend.eip.service.IntegrationEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integration/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Integration Platform Enterprise Addendum API", description = "Endpoints for Connector Marketplace templates, Debezium CDC event streams, EDI X12/AS2 B2B messages, and MDM Golden Master Records")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class IntegrationEnterpriseController {

    private final IntegrationEnterpriseService enterpriseService;

    @PostMapping("/connectors/certify")
    @Operation(summary = "Certify Connector Marketplace Template", description = "Certifies connector template for enterprise distribution in the connector marketplace.")
    public ResponseEntity<ConnectorMarketplace> certifyTemplate(
            @RequestParam String templateName,
            @RequestParam String category,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String version) {
        return ResponseEntity.ok(enterpriseService.certifyConnectorTemplate(templateName, category, publisher, version));
    }

    @PostMapping("/cdc")
    @Operation(summary = "Configure Debezium CDC Stream", description = "Configures real-time database Change Data Capture outbox stream for Kafka event publishing.")
    public ResponseEntity<CdcStream> configureCdc(
            @RequestParam String streamName,
            @RequestParam String sourceTable,
            @RequestParam String kafkaTopic) {
        return ResponseEntity.ok(enterpriseService.configureCdcStream(streamName, sourceTable, kafkaTopic));
    }

    @PostMapping("/edi/send")
    @Operation(summary = "Process EDI X12 / AS2 B2B Message", description = "Exchanges encrypted EDI 850 Purchase Orders or EDI 810 Invoices via AS2 protocol.")
    public ResponseEntity<B2bMessage> sendEdi(
            @RequestParam UUID partnerId,
            @RequestParam String messageType,
            @RequestParam String controlNumber) {
        return ResponseEntity.ok(enterpriseService.sendEdiMessage(partnerId, messageType, controlNumber));
    }

    @PostMapping("/master-data")
    @Operation(summary = "Sync MDM Golden Master Record", description = "Synchronizes single-source-of-truth Golden Records across Merchant, Product, Customer, and Inventory domains.")
    public ResponseEntity<MasterDataRegistry> syncMasterData(
            @RequestParam String domainType,
            @RequestParam String masterCode,
            @RequestBody String goldenRecordJson) {
        return ResponseEntity.ok(enterpriseService.syncGoldenMasterRecord(domainType, masterCode, goldenRecordJson));
    }

    @GetMapping("/marketplace")
    @Operation(summary = "Get Connector Marketplace Templates", description = "Lists certified B2B integration connector templates available for self-service tenant installation.")
    public ResponseEntity<List<ConnectorMarketplace>> getMarketplace() {
        return ResponseEntity.ok(enterpriseService.getMarketplaceTemplates());
    }
}
