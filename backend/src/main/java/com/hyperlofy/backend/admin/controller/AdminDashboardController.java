package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminExecutiveDashboardDTO;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Operations Dashboard API", description = "Endpoints for executive system overview, order metrics, user counts, and platform revenue")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminDashboardController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "Get Admin Executive Dashboard", description = "Retrieves consolidated operational statistics across orders, customers, merchants, delivery partners, revenue, and system health.")
    public ResponseEntity<AdminExecutiveDashboardDTO> getExecutiveDashboard() {
        return ResponseEntity.ok(adminPlatformService.getExecutiveDashboard());
    }
}
