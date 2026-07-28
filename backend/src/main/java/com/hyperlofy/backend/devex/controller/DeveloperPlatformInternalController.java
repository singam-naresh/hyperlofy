package com.hyperlofy.backend.devex.controller;

import com.hyperlofy.backend.devex.entity.ApiKey;
import com.hyperlofy.backend.devex.entity.ApiRoute;
import com.hyperlofy.backend.devex.entity.EventCatalogItem;
import com.hyperlofy.backend.devex.entity.ServiceCatalogItem;
import com.hyperlofy.backend.devex.service.DeveloperPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/devex/internal")
@RequiredArgsConstructor
@Tag(name = "Developer Platform & API Gateway Internal API", description = "Endpoints for dynamic API route registration, developer API key issuance, IDP service cataloging, and AsyncAPI event schema publishing")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DeveloperPlatformInternalController {

    private final DeveloperPlatformService devPlatformService;

    @PostMapping("/routes/register")
    @Operation(summary = "Register API Gateway Route", description = "Dynamically updates Spring Cloud Gateway routing table with path patterns and target cluster URIs.")
    public ResponseEntity<ApiRoute> registerRoute(
            @RequestParam String routeId,
            @RequestParam String serviceName,
            @RequestParam String pathPattern,
            @RequestParam String targetUri) {
        return ResponseEntity.ok(devPlatformService.registerGatewayRoute(routeId, serviceName, pathPattern, targetUri));
    }

    @PostMapping("/keys/issue")
    @Operation(summary = "Issue Developer Portal API Key", description = "Generates authenticated API Key for external developers or internal consumers with daily quota limits.")
    public ResponseEntity<ApiKey> issueApiKey(
            @RequestParam String consumerName,
            @RequestParam String developerEmail,
            @RequestParam(required = false) Integer dailyQuota) {
        return ResponseEntity.ok(devPlatformService.issueApiKey(consumerName, developerEmail, dailyQuota));
    }

    @PostMapping("/services/register")
    @Operation(summary = "Register Service in IDP Catalog", description = "Publishes microservice golden path metadata, repository URL, and owner team into Backstage/Internal Developer Portal catalog.")
    public ResponseEntity<ServiceCatalogItem> registerService(
            @RequestParam String serviceName,
            @RequestParam String description,
            @RequestParam String ownerTeam,
            @RequestParam String repoUrl) {
        return ResponseEntity.ok(devPlatformService.registerServiceCatalog(serviceName, description, ownerTeam, repoUrl));
    }

    @PostMapping("/events/schema")
    @Operation(summary = "Publish AsyncAPI Event Schema", description = "Registers Kafka event schema, topic name, and producing microservice in Event Portal catalog.")
    public ResponseEntity<EventCatalogItem> publishEventSchema(
            @RequestParam String eventName,
            @RequestParam String kafkaTopic,
            @RequestParam String producingService) {
        return ResponseEntity.ok(devPlatformService.registerEventSchema(eventName, kafkaTopic, producingService));
    }
}
