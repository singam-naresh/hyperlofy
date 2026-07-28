package com.hyperlofy.backend.devex.controller;

import com.hyperlofy.backend.devex.entity.ApiContract;
import com.hyperlofy.backend.devex.entity.PartnerApplication;
import com.hyperlofy.backend.devex.entity.PartnerWebhook;
import com.hyperlofy.backend.devex.entity.ServiceScorecard;
import com.hyperlofy.backend.devex.service.DeveloperEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devex/enterprise")
@RequiredArgsConstructor
@Tag(name = "Developer Platform Enterprise Addendum API", description = "Endpoints for API Governance, Consumer Contracts, Partner Ecosystem Management, Signed Webhooks, and Golden Path Service Scorecards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DeveloperEnterpriseController {

    private final DeveloperEnterpriseService enterpriseService;

    @PostMapping("/contracts/register")
    @Operation(summary = "Register Consumer-Driven API Contract", description = "Validates schema compatibility and consumer expectations before deploying breaking API updates.")
    public ResponseEntity<ApiContract> registerContract(
            @RequestParam String contractName,
            @RequestParam String apiName,
            @RequestParam String consumerService,
            @RequestParam(required = false) String schemaVersion) {
        return ResponseEntity.ok(enterpriseService.registerConsumerContract(contractName, apiName, consumerService, schemaVersion));
    }

    @PostMapping("/partner/register")
    @Operation(summary = "Register Partner Application", description = "Onboards external B2B partner application with client ID credentials and API permissions.")
    public ResponseEntity<PartnerApplication> registerPartner(
            @RequestParam String partnerName,
            @RequestParam String appName,
            @RequestParam String contactEmail) {
        return ResponseEntity.ok(enterpriseService.registerPartnerApplication(partnerName, appName, contactEmail));
    }

    @PostMapping("/webhooks/register")
    @Operation(summary = "Register Signed Partner Webhook", description = "Configures signed webhook delivery URL, HMAC secret key, and event subscription types.")
    public ResponseEntity<PartnerWebhook> registerWebhook(
            @RequestParam UUID partnerAppId,
            @RequestParam String targetUrl,
            @RequestParam String eventType) {
        return ResponseEntity.ok(enterpriseService.registerPartnerWebhook(partnerAppId, targetUrl, eventType));
    }

    @PostMapping("/scorecards/compute")
    @Operation(summary = "Compute IDP Golden Path Service Scorecard", description = "Evaluates microservice security compliance, observability coverage, and OpenAPI documentation quality.")
    public ResponseEntity<ServiceScorecard> computeScorecard(
            @RequestParam String serviceName,
            @RequestParam BigDecimal securityScore,
            @RequestParam BigDecimal observabilityScore,
            @RequestParam BigDecimal documentationScore) {
        return ResponseEntity.ok(enterpriseService.computeServiceScorecard(serviceName, securityScore, observabilityScore, documentationScore));
    }
}
