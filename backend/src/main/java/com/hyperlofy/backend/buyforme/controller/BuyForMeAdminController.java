package com.hyperlofy.backend.buyforme.controller;

import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import com.hyperlofy.backend.buyforme.service.BuyForMeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buy-for-me/admin")
@RequiredArgsConstructor
@Tag(name = "Buy For Me Admin Management API", description = "Endpoints for platform administrators to inspect orders, force driver assignment, cancel orders, or process refunds")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class BuyForMeAdminController {

    private final BuyForMeService buyForMeService;

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get Order Details", description = "Fetches complete Buy For Me order details, driver assignment, and purchase proofs.")
    public ResponseEntity<BuyForMeOrder> getOrderDetails(@PathVariable UUID orderId) {
        return ResponseEntity.ok(buyForMeService.getOrderById(orderId));
    }

    @PostMapping("/orders/{orderId}/status")
    @Operation(summary = "Force Update Order Status", description = "Allows administrators to override order state (e.g. CANCELLED, REFUNDED).")
    public ResponseEntity<BuyForMeOrder> updateStatus(@PathVariable UUID orderId, @RequestParam String newStatus) {
        return ResponseEntity.ok(buyForMeService.updateStatus(orderId, newStatus));
    }
}
