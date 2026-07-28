package com.hyperlofy.backend.pickupdrop.controller;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import com.hyperlofy.backend.pickupdrop.service.PickupDropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pickup-drop/admin")
@RequiredArgsConstructor
@Tag(name = "Pickup & Drop Admin API", description = "Endpoints for platform administrators to inspect orders, force driver assignment, manage returns, or resolve disputes")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class PickupDropAdminController {

    private final PickupDropService pickupDropService;

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get Order Details", description = "Fetches complete Pickup & Drop parcel order details and OTP verification state.")
    public ResponseEntity<PickupDropOrder> getOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(pickupDropService.getOrderById(orderId));
    }

    @PostMapping("/orders/{orderId}/status")
    @Operation(summary = "Force Update Order Status", description = "Allows administrators to override parcel order state (e.g. RETURN_IN_PROGRESS, FAILED_DELIVERY).")
    public ResponseEntity<PickupDropOrder> updateStatus(@PathVariable UUID orderId, @RequestParam String newStatus) {
        return ResponseEntity.ok(pickupDropService.updateStatus(orderId, newStatus));
    }
}
