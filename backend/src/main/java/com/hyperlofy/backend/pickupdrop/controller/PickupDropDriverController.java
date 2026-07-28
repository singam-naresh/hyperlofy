package com.hyperlofy.backend.pickupdrop.controller;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import com.hyperlofy.backend.pickupdrop.service.PickupDropService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pickup-drop/driver")
@RequiredArgsConstructor
@Tag(name = "Pickup & Drop Driver API", description = "Endpoints for delivery partners to accept jobs, verify Pickup & Delivery OTP codes, and complete parcel handovers")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class PickupDropDriverController {

    private final PickupDropService pickupDropService;

    @PostMapping("/orders/{orderId}/accept")
    @Operation(summary = "Accept Delivery Job", description = "Delivery partner accepts a Pickup & Drop parcel request.")
    public ResponseEntity<PickupDropOrder> acceptJob(@PathVariable UUID orderId, @RequestParam UUID driverId) {
        return ResponseEntity.ok(pickupDropService.assignDriver(orderId, driverId));
    }

    @PostMapping("/orders/{orderId}/arrive-pickup")
    @Operation(summary = "Confirm Pickup Arrival", description = "Updates order status to ARRIVED_AT_PICKUP.")
    public ResponseEntity<PickupDropOrder> arrivePickup(@PathVariable UUID orderId) {
        return ResponseEntity.ok(pickupDropService.updateStatus(orderId, "ARRIVED_AT_PICKUP"));
    }

    @PostMapping("/orders/{orderId}/verify-pickup-otp")
    @Operation(summary = "Verify Pickup OTP", description = "Verifies 6-digit OTP provided by the sender before picking up the parcel.")
    public ResponseEntity<Map<String, Object>> verifyPickupOtp(@PathVariable UUID orderId, @RequestParam String otpCode) {
        boolean verified = pickupDropService.verifyOtp(orderId, "PICKUP", otpCode);
        return ResponseEntity.ok(Map.of("orderId", orderId, "verified", verified, "status", verified ? "PICKED_UP" : "OTP_INVALID"));
    }

    @PostMapping("/orders/{orderId}/start-transit")
    @Operation(summary = "Start Parcel Transit", description = "Updates order status to IN_TRANSIT.")
    public ResponseEntity<PickupDropOrder> startTransit(@PathVariable UUID orderId) {
        return ResponseEntity.ok(pickupDropService.updateStatus(orderId, "IN_TRANSIT"));
    }

    @PostMapping("/orders/{orderId}/arrive-destination")
    @Operation(summary = "Confirm Destination Arrival", description = "Updates order status to ARRIVED_AT_DESTINATION.")
    public ResponseEntity<PickupDropOrder> arriveDestination(@PathVariable UUID orderId) {
        return ResponseEntity.ok(pickupDropService.updateStatus(orderId, "ARRIVED_AT_DESTINATION"));
    }

    @PostMapping("/orders/{orderId}/verify-delivery-otp")
    @Operation(summary = "Verify Delivery OTP", description = "Verifies 6-digit OTP provided by the recipient to complete final handover.")
    public ResponseEntity<Map<String, Object>> verifyDeliveryOtp(@PathVariable UUID orderId, @RequestParam String otpCode) {
        boolean verified = pickupDropService.verifyOtp(orderId, "DELIVERY", otpCode);
        return ResponseEntity.ok(Map.of("orderId", orderId, "verified", verified, "status", verified ? "DELIVERED" : "OTP_INVALID"));
    }
}
