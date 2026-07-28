package com.hyperlofy.backend.devex.service;

import com.hyperlofy.backend.devex.entity.ApiKey;
import com.hyperlofy.backend.devex.entity.ApiRoute;
import com.hyperlofy.backend.devex.entity.EventCatalogItem;
import com.hyperlofy.backend.devex.entity.ServiceCatalogItem;
import com.hyperlofy.backend.devex.repository.ApiKeyRepository;
import com.hyperlofy.backend.devex.repository.ApiRouteRepository;
import com.hyperlofy.backend.devex.repository.EventCatalogItemRepository;
import com.hyperlofy.backend.devex.repository.ServiceCatalogItemRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeveloperPlatformService {

    private static final Logger log = LoggerFactory.getLogger(DeveloperPlatformService.class);

    private final ApiRouteRepository routeRepository;
    private final ApiKeyRepository keyRepository;
    private final ServiceCatalogItemRepository serviceCatalogRepository;
    private final EventCatalogItemRepository eventCatalogRepository;

    @Transactional
    public ApiRoute registerGatewayRoute(String routeId, String serviceName, String pathPattern, String targetUri) {
        log.info("[DEVELOPER PLATFORM] Registering Spring Cloud Gateway Route RouteId={}, Service={}, Pattern={}",
                routeId, serviceName, pathPattern);

        ApiRoute route = routeRepository.findByRouteId(routeId).orElseGet(() ->
                ApiRoute.builder()
                        .routeId(routeId)
                        .serviceName(serviceName)
                        .pathPattern(pathPattern)
                        .targetUri(targetUri)
                        .rateLimitPerMin(1000)
                        .isActive(true)
                        .build()
        );

        route.setPathPattern(pathPattern);
        route.setTargetUri(targetUri);
        return routeRepository.save(route);
    }

    @Transactional
    public ApiKey issueApiKey(String consumerName, String developerEmail, Integer dailyQuota) {
        log.info("[DEVELOPER PLATFORM] Issuing Developer Portal API Key Consumer={}, Email={}", consumerName, developerEmail);

        String rawKey = "hyp_live_" + UUID.randomUUID().toString().replace("-", "");

        ApiKey key = ApiKey.builder()
                .keyValue(rawKey)
                .consumerName(consumerName)
                .developerEmail(developerEmail)
                .status("ACTIVE")
                .quotaDaily(dailyQuota != null ? dailyQuota : 50000)
                .build();

        return keyRepository.save(key);
    }

    @Transactional
    public ServiceCatalogItem registerServiceCatalog(String serviceName, String description, String ownerTeam, String repoUrl) {
        log.info("[DEVELOPER PLATFORM] Registering Microservice in IDP Service Catalog Name={}, Team={}", serviceName, ownerTeam);

        ServiceCatalogItem item = serviceCatalogRepository.findByServiceName(serviceName).orElseGet(() ->
                ServiceCatalogItem.builder()
                        .serviceName(serviceName)
                        .description(description)
                        .ownerTeam(ownerTeam)
                        .repositoryUrl(repoUrl)
                        .techStack("Java 21 / Spring Boot 3")
                        .build()
        );

        item.setDescription(description);
        item.setRepositoryUrl(repoUrl);
        return serviceCatalogRepository.save(item);
    }

    @Transactional
    public EventCatalogItem registerEventSchema(String eventName, String kafkaTopic, String producingService) {
        log.info("[DEVELOPER PLATFORM] Registering Event Schema in AsyncAPI Event Catalog Event={}, Topic={}", eventName, kafkaTopic);

        EventCatalogItem item = eventCatalogRepository.findByEventName(eventName).orElseGet(() ->
                EventCatalogItem.builder()
                        .eventName(eventName)
                        .kafkaTopic(kafkaTopic)
                        .schemaVersion("v1.0.0")
                        .producingService(producingService)
                        .build()
        );

        return eventCatalogRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<ServiceCatalogItem> getServiceCatalog() {
        return serviceCatalogRepository.findAll();
    }
}
