package com.hyperlofy.backend.buyforme.controller;

import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import com.hyperlofy.backend.buyforme.entity.BuyForMePurchaseProof;
import com.hyperlofy.backend.buyforme.service.BuyForMeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buy-for-me/driver")
@RequiredArgsConstructor
@Tag(name = "Buy For Me Driver API", description = "Endpoints for delivery partners to accept requests, upload store purchase proof, and complete order delivery")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class BuyForMeDriverController {

    private final BuyForMeService buyForMeService;

    @PostMapping("/orders/{orderId}/accept")
    @Operation(summary = "Accept Purchase Request", description = "Delivery partner accepts a customer's Buy For Me request.")
    public ResponseEntity<BuyForMeOrder> acceptRequest(@PathVariable UUID orderId, @RequestParam UUID driverId) {
        return ResponseEntity.ok(buyForMeService.assignDriver(orderId, driverId));
    }

    @PostMapping("/orders/{orderId}/proof")
    @Operation(summary = "Upload Purchase Proof", description = "Uploads store name, address, invoice number, and bill receipt amount for customer verification.")
    public ResponseEntity<BuyForMePurchaseProof> uploadProof(
            @PathVariable UUID orderId,
            @RequestParam UUID driverId,
            @RequestParam String storeName,
            @RequestParam Double billAmount) {
        return ResponseEntity.ok(buyForMeService.uploadPurchaseProof(orderId, driverId, storeName, billAmount));
    }

    @PostMapping("/orders/{orderId}/start-delivery")
    @Operation(summary = "Start Order Transit", description = "Marks the order as IN_TRANSIT after customer approves purchase proof.")
    public ResponseEntity<BuyForMeOrder> startDelivery(@PathVariable UUID orderId) {
        return ResponseEntity.ok(buyForMeService.updateStatus(orderId, "IN_TRANSIT"));
    }

    @PostMapping("/orders/{orderId}/complete")
    @Operation(summary = "Complete Order Delivery", description = "Marks the order as DELIVERED upon reaching customer location.")
    public ResponseEntity<BuyForMeOrder> completeDelivery(@PathVariable UUID orderId) {
        return ResponseEntity.ok(buyForMeService.updateStatus(orderId, "DELIVERED"));
    }
}
