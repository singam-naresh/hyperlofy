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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unified-orders/customer")
@RequiredArgsConstructor
@Tag(name = "Unified Order Customer API", description = "Single source of truth endpoints for customers to view unified orders and chronological timelines across all services")
@PreAuthorize("hasRole('CUSTOMER')")
public class UnifiedOrderCustomerController {

    private final UnifiedOrderService unifiedOrderService;

    @GetMapping("/orders")
    @Operation(summary = "Get Customer Master Orders", description = "Fetches unified orders across Marketplace, Buy For Me, and Pickup & Drop services for a customer.")
    public ResponseEntity<List<MasterOrder>> getCustomerOrders(@RequestParam UUID customerId) {
        return ResponseEntity.ok(unifiedOrderService.getCustomerOrders(customerId));
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get Master Order Details", description = "Fetches global master order details by UUID.")
    public ResponseEntity<MasterOrder> getOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(unifiedOrderService.getOrderById(orderId));
    }

    @GetMapping("/orders/{orderId}/timeline")
    @Operation(summary = "Get Order Timeline History", description = "Fetches chronological lifecycle events and milestone updates for an order.")
    public ResponseEntity<List<OrderTimeline>> getOrderTimeline(@PathVariable UUID orderId) {
        return ResponseEntity.ok(unifiedOrderService.getOrderTimeline(orderId));
    }
}
