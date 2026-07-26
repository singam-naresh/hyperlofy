package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.merchant.dto.MerchantOrderResponseDTO;
import com.hyperlofy.backend.merchant.service.MerchantPortalService;
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
@RequestMapping("/api/v1/merchant/orders")
@RequiredArgsConstructor
@Tag(name = "Merchant Order Management API", description = "Endpoints for merchant order lifecycle processing, state transitions, and filtering")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantOrderController {

    private final MerchantPortalService merchantPortalService;

    @GetMapping
    @Operation(summary = "List Merchant Orders", description = "Paginated lookup for merchant orders supporting status filtering and search.")
    public ResponseEntity<Page<MerchantOrderResponseDTO>> getMerchantOrders(
            @RequestParam UUID merchantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(merchantPortalService.getMerchantOrders(merchantId, page, size, status, search));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Merchant Order Details", description = "Retrieves complete order details with item breakdown for an owned order.")
    public ResponseEntity<MerchantOrderResponseDTO> getOrderById(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(merchantPortalService.getMerchantOrderById(merchantId, orderId));
    }

    @PatchMapping("/{orderId}/accept")
    @Operation(summary = "Accept Order", description = "Merchant accepts an incoming order.")
    public ResponseEntity<MerchantOrderResponseDTO> acceptOrder(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(merchantPortalService.acceptOrder(merchantId, orderId));
    }

    @PatchMapping("/{orderId}/reject")
    @Operation(summary = "Reject Order", description = "Merchant rejects an incoming order and triggers escrow refund.")
    public ResponseEntity<MerchantOrderResponseDTO> rejectOrder(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "Merchant rejected order") String reason) {

        return ResponseEntity.ok(merchantPortalService.rejectOrder(merchantId, orderId, reason));
    }

    @PatchMapping("/{orderId}/preparing")
    @Operation(summary = "Mark Order Preparing", description = "Merchant marks order preparation in progress.")
    public ResponseEntity<MerchantOrderResponseDTO> markPreparing(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(merchantPortalService.markOrderPreparing(merchantId, orderId));
    }

    @PatchMapping("/{orderId}/ready")
    @Operation(summary = "Mark Order Ready For Pickup", description = "Merchant marks order ready for delivery agent pickup.")
    public ResponseEntity<MerchantOrderResponseDTO> markReady(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId) {

        return ResponseEntity.ok(merchantPortalService.markOrderReady(merchantId, orderId));
    }

    @PatchMapping("/{orderId}/out-of-stock")
    @Operation(summary = "Mark Order Out Of Stock", description = "Merchant flags order out of stock and cancels with customer refund.")
    public ResponseEntity<MerchantOrderResponseDTO> markOutOfStock(
            @RequestParam UUID merchantId,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "Item out of stock") String itemDetails) {

        return ResponseEntity.ok(merchantPortalService.markOrderOutOfStock(merchantId, orderId, itemDetails));
    }
}
