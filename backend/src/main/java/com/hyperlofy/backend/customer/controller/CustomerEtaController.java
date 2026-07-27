package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.ai.logistics.entity.EtaPrediction;
import com.hyperlofy.backend.ai.logistics.service.DeliveryIntelligenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/eta")
@RequiredArgsConstructor
@Tag(name = "Customer Real-Time ETA API", description = "Endpoints for live order ETA predictions and delivery time confidence bounds")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerEtaController {

    private final DeliveryIntelligenceService deliveryIntelligenceService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get Live Order ETA", description = "Retrieves live estimated delivery arrival time, preparation breakdown, and travel estimates.")
    public ResponseEntity<EtaPrediction> getOrderEta(@PathVariable UUID orderId) {
        return ResponseEntity.ok(deliveryIntelligenceService.calculateOrderEta(orderId));
    }
}
