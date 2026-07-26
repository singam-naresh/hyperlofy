package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.dto.DeliveryOrderResponseDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
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
@RequestMapping("/api/v1/delivery/orders")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Order Management API", description = "Endpoints for delivery partner order lifecycle processing, pickup, transit, and delivery completion")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryOrderController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping
    @Operation(summary = "List Delivery Orders", description = "Paginated list of delivery orders assigned to the partner with status and search filtering.")
    public ResponseEntity<Page<DeliveryOrderResponseDTO>> getAgentOrders(
            @RequestParam UUID agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(deliveryPlatformService.getAgentOrders(agentId, page, size, status, search));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Delivery Order Details", description = "Retrieves complete order trip details, store location, customer address, and items.")
    public ResponseEntity<DeliveryOrderResponseDTO> getOrderById(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.getAgentOrderById(agentId, orderId));
    }

    @PatchMapping("/{orderId}/accept")
    @Operation(summary = "Accept Delivery Assignment", description = "Delivery partner accepts an assigned delivery order.")
    public ResponseEntity<DeliveryOrderResponseDTO> acceptOrder(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.acceptOrder(agentId, orderId));
    }

    @PatchMapping("/{orderId}/reject")
    @Operation(summary = "Reject Delivery Assignment", description = "Delivery partner rejects an assigned delivery order.")
    public ResponseEntity<DeliveryOrderResponseDTO> rejectOrder(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "Partner rejected delivery") String reason) {

        return ResponseEntity.ok(deliveryPlatformService.rejectOrder(agentId, orderId, reason));
    }

    @PatchMapping("/{orderId}/arrived-merchant")
    @Operation(summary = "Mark Arrived at Merchant Store", description = "Partner flags arrival at the merchant store location.")
    public ResponseEntity<DeliveryOrderResponseDTO> markArrivedMerchant(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.markArrivedMerchant(agentId, orderId));
    }

    @PatchMapping("/{orderId}/picked-up")
    @Operation(summary = "Mark Order Picked Up", description = "Partner confirms pickup of items from store.")
    public ResponseEntity<DeliveryOrderResponseDTO> markPickedUp(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.markPickedUp(agentId, orderId));
    }

    @PatchMapping("/{orderId}/out-for-delivery")
    @Operation(summary = "Mark Out for Delivery", description = "Partner marks delivery in transit to customer location.")
    public ResponseEntity<DeliveryOrderResponseDTO> markOutForDelivery(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.markOutForDelivery(agentId, orderId));
    }

    @PatchMapping("/{orderId}/arrived-customer")
    @Operation(summary = "Mark Arrived at Customer Location", description = "Partner flags arrival at the customer delivery address.")
    public ResponseEntity<DeliveryOrderResponseDTO> markArrivedCustomer(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(deliveryPlatformService.markArrivedCustomer(agentId, orderId));
    }

    @PatchMapping("/{orderId}/complete")
    @Operation(summary = "Complete Delivery", description = "Partner completes delivery with optional OTP verification, triggering automated escrow release.")
    public ResponseEntity<DeliveryOrderResponseDTO> completeOrder(
            @RequestParam UUID agentId,
            @PathVariable UUID orderId,
            @RequestParam(required = false) String otpCode) {

        return ResponseEntity.ok(deliveryPlatformService.completeOrder(agentId, orderId, otpCode));
    }
}
