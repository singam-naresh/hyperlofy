package com.hyperlofy.backend.inventory.controller;

import com.hyperlofy.backend.inventory.dto.InventoryAvailabilityResult;
import com.hyperlofy.backend.inventory.dto.InventoryReservationRequest;
import com.hyperlofy.backend.inventory.dto.InventoryReservationResult;
import com.hyperlofy.backend.inventory.service.InventoryReservationService;
import com.hyperlofy.backend.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryReservationService reservationService;

    @GetMapping("/{merchantId}/{productId}")
    public ResponseEntity<InventoryAvailabilityResult> getByProduct(@PathVariable UUID merchantId, @PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryService.checkAvailability(merchantId, productId));
    }

    @GetMapping("/sku/{merchantId}/{sku}")
    public ResponseEntity<InventoryAvailabilityResult> getBySku(@PathVariable UUID merchantId, @PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.checkAvailabilityBySku(merchantId, sku));
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryReservationResult> reserve(@Valid @RequestBody InventoryReservationRequest req) {
        return ResponseEntity.ok(reservationService.reserveInventory(req));
    }

    @PostMapping("/release")
    public ResponseEntity<InventoryReservationResult> release(@Valid @RequestBody InventoryReservationRequest req) {
        if (req.getReservationId() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(reservationService.releaseReservation(req.getReservationId()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<InventoryReservationResult> confirm(@Valid @RequestBody InventoryReservationRequest req) {
        if (req.getReservationId() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(reservationService.confirmReservation(req.getReservationId()));
    }
}
