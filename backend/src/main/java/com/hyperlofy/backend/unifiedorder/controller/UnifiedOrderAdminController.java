package com.hyperlofy.backend.unifiedorder.controller;

import com.hyperlofy.backend.unifiedorder.entity.MasterOrder;
import com.hyperlofy.backend.unifiedorder.service.UnifiedOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unified-orders/admin")
@RequiredArgsConstructor
@Tag(name = "Unified Order Admin Management API", description = "Endpoints for platform administrators to inspect global master orders, force status updates, and audit timeline history")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UnifiedOrderAdminController {

    private final UnifiedOrderService unifiedOrderService;

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get Master Order Inspection Details", description = "Returns full details of any master order across all business verticals.")
    public ResponseEntity<MasterOrder> getOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(unifiedOrderService.getOrderById(orderId));
    }

    @PostMapping("/orders/{orderId}/status")
    @Operation(summary = "Admin Force Update Master Order Status", description = "Allows administrators to override master order state (e.g. CANCELLED, REFUNDED).")
    public ResponseEntity<MasterOrder> updateStatus(
            @PathVariable UUID orderId,
            @RequestParam String newStatus,
            @RequestParam String changeReason) {
        return ResponseEntity.ok(unifiedOrderService.updateOrderStatus(orderId, newStatus, changeReason, "ADMIN_OPERATOR", "ADMIN", "ADMIN_CONSOLE"));
    }
}
