package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminCustomerResponseDTO;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
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
@RequestMapping("/api/v1/admin/customers")
@RequiredArgsConstructor
@Tag(name = "Admin Customer Administration API", description = "Endpoints for customer account auditing, blocking, unblocking, and order history inspection")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminCustomerController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "List Customers", description = "Paginated list of registered platform customers with search filtering.")
    public ResponseEntity<Page<AdminCustomerResponseDTO>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(adminPlatformService.getCustomers(page, size, search));
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get Customer Details", description = "Retrieves customer profile and account status.")
    public ResponseEntity<AdminCustomerResponseDTO> getCustomerById(@PathVariable UUID customerId) {
        return ResponseEntity.ok(adminPlatformService.getCustomerById(customerId));
    }

    @PatchMapping("/{customerId}/block")
    @Operation(summary = "Block Customer Account", description = "Blocks a customer account.")
    public ResponseEntity<AdminCustomerResponseDTO> blockCustomer(
            Principal principal,
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "Fraud or safety block") String reason) {

        return ResponseEntity.ok(adminPlatformService.setCustomerBlocked(UUID.randomUUID(), principal.getName(), customerId, true, reason));
    }

    @PatchMapping("/{customerId}/unblock")
    @Operation(summary = "Unblock Customer Account", description = "Unblocks a customer account.")
    public ResponseEntity<AdminCustomerResponseDTO> unblockCustomer(Principal principal, @PathVariable UUID customerId) {
        return ResponseEntity.ok(adminPlatformService.setCustomerBlocked(UUID.randomUUID(), principal.getName(), customerId, false, "Admin unblocked customer"));
    }
}
