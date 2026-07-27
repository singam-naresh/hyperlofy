package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import com.hyperlofy.backend.order.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/orders")
@RequiredArgsConstructor
@Tag(name = "Customer Order Experience API", description = "Endpoints for current/past orders, order tracking, escrow status, and reordering")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerOrderController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Customer Orders History", description = "Retrieves order history for authenticated customer.")
    public ResponseEntity<List<Order>> getCustomerOrders(Principal principal) {
        return ResponseEntity.ok(customerService.getCustomerOrders(UUID.randomUUID()));
    }
}
