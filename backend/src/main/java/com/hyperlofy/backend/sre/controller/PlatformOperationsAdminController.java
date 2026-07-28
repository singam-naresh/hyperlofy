package com.hyperlofy.backend.sre.controller;

import com.hyperlofy.backend.sre.entity.PlatformHealth;
import com.hyperlofy.backend.sre.service.PlatformOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sre/admin")
@RequiredArgsConstructor
@Tag(name = "SRE & Cloud Operations Admin API", description = "Endpoints for Principal Site Reliability Engineers and Cloud Architects to oversee multi-region cluster health and service readiness")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PlatformOperationsAdminController {

    private final PlatformOperationsService sreService;

    @GetMapping("/health")
    @Operation(summary = "Get Multi-Cluster Overall Platform Health", description = "Returns CPU/Memory metrics, probe status, and P95 latencies across all 18 Hyperlofy backend engines.")
    public ResponseEntity<List<PlatformHealth>> getPlatformHealth() {
        return ResponseEntity.ok(sreService.getOverallPlatformHealth());
    }
}
