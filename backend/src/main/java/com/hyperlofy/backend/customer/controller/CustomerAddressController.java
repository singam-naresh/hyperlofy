package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.entity.CustomerAddress;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/addresses")
@RequiredArgsConstructor
@Tag(name = "Customer Address Management API", description = "Endpoints for customer delivery addresses, GPS coordinates, and default address selection")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerAddressController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Customer Addresses", description = "Retrieves saved delivery addresses for authenticated customer.")
    public ResponseEntity<List<CustomerAddress>> getAddresses(Principal principal) {
        return ResponseEntity.ok(customerService.getAddresses(UUID.randomUUID()));
    }

    @PostMapping
    @Operation(summary = "Save Delivery Address", description = "Creates or updates a customer delivery address.")
    public ResponseEntity<CustomerAddress> saveAddress(Principal principal, @Valid @RequestBody CustomerAddress address) {
        return ResponseEntity.ok(customerService.saveAddress(UUID.randomUUID(), address));
    }
}
