package com.hyperlofy.backend.unifiedorder.controller;

import com.hyperlofy.backend.unifiedorder.entity.MasterOrder;
import com.hyperlofy.backend.unifiedorder.entity.OrderTimeline;
import com.hyperlofy.backend.unifiedorder.service.UnifiedOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unified-orders/internal")
@RequiredArgsConstructor
@Tag(name = "Unified Order Internal Service Integration API", description = "Endpoints for microservices (Marketplace, Buy For Me, Pickup & Drop) to register master orders and publish timeline events")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class UnifiedOrderInternalController {

    private final UnifiedOrderService unifiedOrderService;

    @PostMapping("/orders")
    @Operation(summary = "Register Master Order", description = "Called by business microservices to create master order entries with idempotency key protection.")
    public ResponseEntity<MasterOrder> registerOrder(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @RequestParam UUID businessOrderId,
            @RequestParam String orderType,
            @RequestParam UUID customerId,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam Double amount,
            @RequestParam String sourceService) {
        return ResponseEntity.ok(unifiedOrderService.registerMasterOrder(idempotencyKey, businessOrderId, orderType, customerId, merchantId, amount, sourceService));
    }

    @PostMapping("/orders/{orderId}/timeline")
    @Operation(summary = "Publish Timeline Event", description = "Adds a chronological milestone event to the master order timeline.")
    public ResponseEntity<OrderTimeline> addTimelineEvent(
            @PathVariable UUID orderId,
            @RequestParam String eventName,
            @RequestParam String actorId,
            @RequestParam String actorType,
            @RequestParam String sourceService,
            @RequestParam String description) {
        return ResponseEntity.ok(unifiedOrderService.addTimelineEvent(orderId, eventName, actorId, actorType, sourceService, description));
    }

    @PostMapping("/orders/{orderId}/status")
    @Operation(summary = "Update Master Order Status", description = "Updates status of the master order across microservices.")
    public ResponseEntity<MasterOrder> updateStatus(
            @PathVariable UUID orderId,
            @RequestParam String newStatus,
            @RequestParam String changeReason,
            @RequestParam String actorId,
            @RequestParam String actorType,
            @RequestParam String sourceService) {
        return ResponseEntity.ok(unifiedOrderService.updateOrderStatus(orderId, newStatus, changeReason, actorId, actorType, sourceService));
    }
}
