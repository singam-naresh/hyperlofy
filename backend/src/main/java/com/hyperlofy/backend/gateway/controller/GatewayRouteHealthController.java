package com.hyperlofy.backend.gateway.controller;

import com.hyperlofy.backend.config.PlatformConfigProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/gateway")
@RequiredArgsConstructor
@Tag(name = "API Gateway Foundation API", description = "Endpoints for gateway health diagnostic status, route configurations, and trace context metadata")
public class GatewayRouteHealthController {

    private final PlatformConfigProperties configProperties;

    @GetMapping("/health")
    @Operation(summary = "Gateway Health Diagnostics", description = "Returns active gateway metrics, current configuration, and status health probes.")
    public ResponseEntity<Map<String, Object>> getGatewayHealth() {
        return ResponseEntity.ok(Map.of(
                "gatewayStatus", "UP",
                "version", "1.0.0",
                "defaultRateLimitPerSecond", configProperties.getGateway().getDefaultRateLimitPerSecond(),
                "correlationHeader", configProperties.getGateway().getCorrelationHeaderName(),
                "traceHeader", configProperties.getGateway().getTraceHeaderName()
        ));
    }
}
