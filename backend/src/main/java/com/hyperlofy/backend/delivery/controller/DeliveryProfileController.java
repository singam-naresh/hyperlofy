package com.hyperlofy.backend.delivery.controller;

import com.hyperlofy.backend.delivery.dto.DeliveryProfileDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery/profile")
@RequiredArgsConstructor
@Tag(name = "Delivery Partner Profile API", description = "Endpoints for partner profile details, vehicle details, driving license, emergency contact, and bank account info")
@PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'SUPER_ADMIN')")
public class DeliveryProfileController {

    private final DeliveryPlatformService deliveryPlatformService;

    @GetMapping
    @Operation(summary = "Get Partner Profile", description = "Retrieves delivery partner profile info, vehicle number, driving license, emergency contact, rating, and bank details.")
    public ResponseEntity<DeliveryProfileDTO> getProfile(@RequestParam UUID agentId) {
        return ResponseEntity.ok(deliveryPlatformService.getProfile(agentId));
    }

    @PutMapping
    @Operation(summary = "Update Partner Profile", description = "Updates delivery partner profile, vehicle info, emergency contact, and bank account info.")
    public ResponseEntity<DeliveryProfileDTO> updateProfile(@RequestParam UUID agentId, @Valid @RequestBody DeliveryProfileDTO dto) {
        return ResponseEntity.ok(deliveryPlatformService.updateProfile(agentId, dto));
    }
}
