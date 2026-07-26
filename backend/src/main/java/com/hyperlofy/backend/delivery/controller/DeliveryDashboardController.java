package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.dto.DeliveryDashboardDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery/dashboard")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Dashboard API", description = "Endpoints for Delivery Partner Dashboard overview, active deliveries, earnings, and ratings")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryDashboardController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping
    @Operation(summary = "Get Delivery Dashboard", description = "Retrieves consolidated delivery partner metrics including today's deliveries, active orders, earnings, wallet balance, and partner rating.")
    public ResponseEntity<DeliveryDashboardDTO> getDashboard(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getDashboard(agentId));
    }
}
