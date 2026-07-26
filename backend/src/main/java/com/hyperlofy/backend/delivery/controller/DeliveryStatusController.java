package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.dto.DeliveryStatusDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Work Availability API", description = "Endpoints for delivery partner work status toggle (Online, Offline, Busy, Break)")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryStatusController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping("/status")
    @Operation(summary = "Get Delivery Work Status", description = "Retrieves current work status and availability flag of delivery partner.")
    public ResponseEntity<DeliveryStatusDTO> getStatus(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getWorkStatus(agentId));
    }

    @PatchMapping("/online")
    @Operation(summary = "Set Work Status Online", description = "Partner sets status to ONLINE and available for dispatch.")
    public ResponseEntity<DeliveryStatusDTO> setOnline(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.updateWorkStatus(agentId, "ONLINE"));
    }

    @PatchMapping("/offline")
    @Operation(summary = "Set Work Status Offline", description = "Partner sets status to OFFLINE and unavailable for dispatch.")
    public ResponseEntity<DeliveryStatusDTO> setOffline(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.updateWorkStatus(agentId, "OFFLINE"));
    }

    @PatchMapping("/break")
    @Operation(summary = "Set Work Status On Break", description = "Partner sets status to ON_BREAK.")
    public ResponseEntity<DeliveryStatusDTO> setBreak(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.updateWorkStatus(agentId, "ON_BREAK"));
    }

    @PatchMapping("/available")
    @Operation(summary = "Set Work Status Available", description = "Partner confirms availability for new delivery assignments.")
    public ResponseEntity<DeliveryStatusDTO> setAvailable(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.updateWorkStatus(agentId, "AVAILABLE"));
    }
}
