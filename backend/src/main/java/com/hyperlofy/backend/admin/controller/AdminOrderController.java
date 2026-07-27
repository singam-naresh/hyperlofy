package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Live Order Monitoring API", description = "Endpoints for platform-wide live order tracking, auditing, and filtering")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminOrderController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "Live Order Monitoring List", description = "Paginated lookup across system orders with filtering by merchant, customer, agent, zone, status, and search.")
    public ResponseEntity<Page<Order>> getLiveOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID agentId,
            @RequestParam(required = false) UUID zoneId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(adminPlatformService.getLiveOrders(page, size, merchantId, customerId, agentId, zoneId, status, search));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Complete Order Details", description = "Retrieves full order audit details including items, customer, delivery agent, and status timeline.")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        return ResponseEntity.ok(adminPlatformService.getOrderById(orderId));
    }
}
