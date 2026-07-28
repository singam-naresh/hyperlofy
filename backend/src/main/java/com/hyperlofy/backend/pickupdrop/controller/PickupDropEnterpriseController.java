package com.hyperlofy.backend.pickupdrop.controller;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropCustodyHistory;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropDriverTransfer;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropInsuranceClaim;
import com.hyperlofy.backend.pickupdrop.service.PickupDropEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pickup-drop/enterprise")
@RequiredArgsConstructor
@Tag(name = "Pickup & Drop Enterprise Addendum API", description = "Endpoints for parcel chain of custody tracking, driver-to-driver handovers, and insurance claim submissions")
public class PickupDropEnterpriseController {

    private final PickupDropEnterpriseService enterpriseService;

    @PostMapping("/orders/{orderId}/custody")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "Record Chain of Custody Event", description = "Logs parcel handover, custody creation, or return verification event with GPS coordinates.")
    public ResponseEntity<PickupDropCustodyHistory> recordCustody(
            @PathVariable UUID orderId,
            @RequestParam String custodyEvent,
            @RequestParam UUID driverId,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(enterpriseService.recordCustodyTransfer(orderId, custodyEvent, driverId, lat, lng, notes));
    }

    @PostMapping("/orders/{orderId}/driver-transfer")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "Initiate Driver-to-Driver Handover", description = "Initiates parcel transfer during vehicle breakdowns or emergency shift changes via 6-digit transfer OTP.")
    public ResponseEntity<PickupDropDriverTransfer> initiateTransfer(
            @PathVariable UUID orderId,
            @RequestParam UUID fromDriverId,
            @RequestParam UUID toDriverId,
            @RequestParam String reason) {
        return ResponseEntity.ok(enterpriseService.initiateDriverTransfer(orderId, fromDriverId, toDriverId, reason));
    }

    @PostMapping("/orders/{orderId}/claims")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Submit Insurance Claim", description = "Submits a claim for damaged or lost insured parcel dispatches with evidence photo URL.")
    public ResponseEntity<PickupDropInsuranceClaim> submitClaim(
            @PathVariable UUID orderId,
            @RequestParam String claimType,
            @RequestParam Double claimedAmount,
            @RequestParam String description,
            @RequestParam(required = false) String evidenceUrl) {
        return ResponseEntity.ok(enterpriseService.submitInsuranceClaim(orderId, claimType, claimedAmount, description, evidenceUrl));
    }

    @GetMapping("/orders/{orderId}/custody")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Track Parcel Chain of Custody", description = "Returns complete immutable audit history of parcel handlers, GPS locations, and timestamps.")
    public ResponseEntity<List<PickupDropCustodyHistory>> getCustodyHistory(@PathVariable UUID orderId) {
        return ResponseEntity.ok(enterpriseService.getCustodyHistory(orderId));
    }
}
