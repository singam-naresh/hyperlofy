package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.delivery.dto.DeliveryAnalyticsDTO;
import com.hyperlofy.backend.delivery.dto.DeliveryEarningsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/delivery-partners")
@RequiredArgsConstructor
@Tag(name = "Admin Delivery Partner Administration API", description = "Endpoints for delivery partner lifecycle management, activation, suspension, earnings, and analytics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminDeliveryController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "List Delivery Partners", description = "Paginated list of delivery partners with search filtering.")
    public ResponseEntity<Page<AgentProfile>> getDeliveryPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(adminPlatformService.getDeliveryPartners(page, size, search));
    }

    @GetMapping("/{agentId}")
    @Operation(summary = "Get Delivery Partner Details", description = "Retrieves delivery partner profile details.")
    public ResponseEntity<AgentProfile> getDeliveryPartnerById(@PathVariable UUID agentId) {
        return ResponseEntity.ok(adminPlatformService.getDeliveryPartnerById(agentId));
    }

    @PatchMapping("/{agentId}/activate")
    @Operation(summary = "Activate Delivery Partner", description = "Activates a delivery partner profile.")
    public ResponseEntity<AgentProfile> activateDeliveryPartner(Principal principal, @PathVariable UUID agentId) {
        return ResponseEntity.ok(adminPlatformService.setDeliveryPartnerActive(UUID.randomUUID(), principal.getName(), agentId, true, "Admin activated delivery partner"));
    }

    @PatchMapping("/{agentId}/suspend")
    @Operation(summary = "Suspend Delivery Partner", description = "Suspends a delivery partner profile.")
    public ResponseEntity<AgentProfile> suspendDeliveryPartner(
            Principal principal,
            @PathVariable UUID agentId,
            @RequestParam(defaultValue = "Administrative action") String reason) {

        return ResponseEntity.ok(adminPlatformService.setDeliveryPartnerActive(UUID.randomUUID(), principal.getName(), agentId, false, reason));
    }

    @PatchMapping("/{agentId}/reactivate")
    @Operation(summary = "Reactivate Delivery Partner", description = "Reactivates a suspended delivery partner profile.")
    public ResponseEntity<AgentProfile> reactivateDeliveryPartner(Principal principal, @PathVariable UUID agentId) {
        return ResponseEntity.ok(adminPlatformService.setDeliveryPartnerActive(UUID.randomUUID(), principal.getName(), agentId, true, "Admin reactivated delivery partner"));
    }

    @GetMapping("/{agentId}/earnings")
    @Operation(summary = "Get Delivery Partner Earnings", description = "Retrieves earnings breakdown for a delivery partner.")
    public ResponseEntity<DeliveryEarningsDTO> getDeliveryPartnerEarnings(@PathVariable UUID agentId) {
        return ResponseEntity.ok(adminPlatformService.getDeliveryPartnerEarnings(agentId));
    }

    @GetMapping("/{agentId}/analytics")
    @Operation(summary = "Get Delivery Partner Analytics", description = "Retrieves performance analytics for a delivery partner.")
    public ResponseEntity<DeliveryAnalyticsDTO> getDeliveryPartnerAnalytics(@PathVariable UUID agentId) {
        return ResponseEntity.ok(adminPlatformService.getDeliveryPartnerAnalytics(agentId));
    }
}
