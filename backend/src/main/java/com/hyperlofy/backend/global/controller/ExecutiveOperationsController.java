package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.ExecutiveOperationsDashboard;
import com.hyperlofy.backend.global.service.GlobalEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/executive")
@RequiredArgsConstructor
@Tag(name = "Executive Global Operations & Sustainability API", description = "Executive C-suite dashboards for 99.99% global availability, RPO/RTO compliance, resilience scorecards, FinOps cost savings, and carbon emissions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ExecutiveOperationsController {

    private final GlobalEnterpriseService enterpriseService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get Executive Global Operations Dashboard", description = "Returns executive metrics: 99.99% availability, 100% RPO/RTO compliance, 98.5 resilience score, FinOps savings ($18.5k), and carbon emissions.")
    public ResponseEntity<ExecutiveOperationsDashboard> getDashboard(
            @RequestParam(required = false, defaultValue = "GLOBAL_MAIN") String key) {
        return ResponseEntity.ok(enterpriseService.getExecutiveDashboard(key));
    }
}
