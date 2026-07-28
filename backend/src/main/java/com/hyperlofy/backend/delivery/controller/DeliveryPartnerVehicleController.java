package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.entity.Vehicle;
import com.hyperlofy.backend.delivery.service.DeliveryPartnerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery/partner")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Management API", description = "Endpoints for vehicle registration, working zone availability, and earnings breakdown")
@PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryPartnerVehicleController {

    private final DeliveryPartnerManagementService partnerService;

    @PostMapping("/vehicle")
    @Operation(summary = "Register Vehicle", description = "Registers vehicle registration number, type, RC, and insurance expiration details.")
    public ResponseEntity<Vehicle> registerVehicle(@RequestBody Vehicle vehicle, Principal principal) {
        vehicle.setDeliveryPartnerId(UUID.randomUUID());
        return ResponseEntity.ok(partnerService.registerVehicle(vehicle));
    }

    @GetMapping("/vehicle")
    @Operation(summary = "Get Partner Vehicle", description = "Retrieves registered vehicle details for the partner.")
    public ResponseEntity<Vehicle> getVehicle(Principal principal) {
        return ResponseEntity.ok(partnerService.getPartnerVehicle(UUID.randomUUID()));
    }

    @GetMapping("/earnings")
    @Operation(summary = "Get Partner Earnings Summary", description = "Retrieves daily, weekly, and monthly earnings breakdown with wallet balance.")
    public ResponseEntity<Map<String, Object>> getEarningsSummary(Principal principal) {
        return ResponseEntity.ok(partnerService.getPartnerEarningsSummary(UUID.randomUUID()));
    }
}
