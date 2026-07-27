package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.dto.PlatformHealthDTO;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform/health")
@RequiredArgsConstructor
@Tag(name = "Platform Health & Observability API", description = "Endpoints for database, API gateway, cache, queue, scheduler, and JVM health metrics")
public class PlatformHealthController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "Get Platform Health Metrics", description = "Retrieves real-time system health metrics, uptime, version, and JVM memory utilization.")
    public ResponseEntity<PlatformHealthDTO> getPlatformHealth() {
        return ResponseEntity.ok(platformService.getPlatformHealth());
    }
}
