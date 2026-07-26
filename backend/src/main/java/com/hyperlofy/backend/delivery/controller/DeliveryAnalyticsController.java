package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.dto.DeliveryAnalyticsDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery/analytics")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Analytics API", description = "Endpoints for delivery partner acceptance rates, completion rates, delivery speed, and earnings trends")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryAnalyticsController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping
    @Operation(summary = "Get Partner Performance Analytics", description = "Retrieves acceptance rate, completion rate, avg delivery time, customer rating, total distance travelled, and revenue trends.")
    public ResponseEntity<DeliveryAnalyticsDTO> getAnalytics(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getAnalytics(agentId));
    }
}
