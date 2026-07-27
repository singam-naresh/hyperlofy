package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.dto.FraudRiskDashboardDTO;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform/fraud-risk")
@RequiredArgsConstructor
@Tag(name = "Platform Fraud & Risk Monitoring API", description = "Endpoints for fraud risk dashboards, duplicate account monitoring, and refund abuse detection")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class FraudRiskController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "Get Fraud & Risk Dashboard", description = "Retrieves read-only fraud risk metrics across duplicate accounts, refund abuse, and payment failure rates.")
    public ResponseEntity<FraudRiskDashboardDTO> getFraudRiskDashboard() {
        return ResponseEntity.ok(platformService.getFraudRiskDashboard());
    }
}
