package com.hyperlofy.backend.pickupdrop.controller;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import com.hyperlofy.backend.pickupdrop.service.PickupDropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pickup-drop/customer")
@RequiredArgsConstructor
@Tag(name = "Pickup & Drop Customer API", description = "Endpoints for creating parcel dispatch requests, tracking delivery progress, and retrieving OTP verification codes")
@PreAuthorize("hasRole('CUSTOMER')")
public class PickupDropCustomerController {

    private final PickupDropService pickupDropService;

    @PostMapping("/deliveries")
    @Operation(summary = "Create Parcel Dispatch Request", description = "Submits a Same-day, Express, or Scheduled parcel pickup and drop delivery order.")
    public ResponseEntity<PickupDropOrder> createDelivery(
            @RequestParam UUID customerId,
            @RequestParam String senderName,
            @RequestParam String senderContact,
            @RequestParam String pickupAddr,
            @RequestParam Double pLat,
            @RequestParam Double pLng,
            @RequestParam String recipientName,
            @RequestParam String recipientContact,
            @RequestParam String delAddr,
            @RequestParam Double dLat,
            @RequestParam Double dLng,
            @RequestParam(defaultValue = "SAME_DAY") String delType) {
        return ResponseEntity.ok(pickupDropService.createDeliveryOrder(customerId, senderName, senderContact, pickupAddr, pLat, pLng,
                recipientName, recipientContact, delAddr, dLat, dLng, delType));
    }

    @GetMapping("/deliveries")
    @Operation(summary = "Get Customer Delivery History", description = "Fetches active and historical parcel dispatches for a customer.")
    public ResponseEntity<List<PickupDropOrder>> getCustomerDeliveries(@RequestParam UUID customerId) {
        return ResponseEntity.ok(pickupDropService.getCustomerOrders(customerId));
    }
}
