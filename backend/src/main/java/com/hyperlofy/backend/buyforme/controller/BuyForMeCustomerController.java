package com.hyperlofy.backend.buyforme.controller;

import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import com.hyperlofy.backend.buyforme.service.BuyForMeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buy-for-me/customer")
@RequiredArgsConstructor
@Tag(name = "Buy For Me Customer API", description = "Endpoints for creating custom purchase requests, approving driver proofs, and tracking order delivery status")
@PreAuthorize("hasRole('CUSTOMER')")
public class BuyForMeCustomerController {

    private final BuyForMeService buyForMeService;

    @PostMapping("/requests")
    @Operation(summary = "Create Custom Purchase Request", description = "Submits a request for custom items (Medicines, Groceries, Food) not listed in the marketplace.")
    public ResponseEntity<BuyForMeOrder> createRequest(
            @RequestParam UUID customerId,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam Double maxBudget,
            @RequestParam String address,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        return ResponseEntity.ok(buyForMeService.createCustomerRequest(customerId, title, category, maxBudget, address, lat, lng));
    }

    @PostMapping("/orders/{orderId}/approve")
    @Operation(summary = "Approve Driver Purchase Proof", description = "Customer approves the receipt image, bill amount, and store details uploaded by the driver.")
    public ResponseEntity<BuyForMeOrder> approvePurchase(@PathVariable UUID orderId) {
        return ResponseEntity.ok(buyForMeService.approvePurchase(orderId));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get Customer Request History", description = "Fetches active and historical Buy For Me orders for a customer.")
    public ResponseEntity<List<BuyForMeOrder>> getCustomerOrders(@RequestParam UUID customerId) {
        return ResponseEntity.ok(buyForMeService.getCustomerOrders(customerId));
    }
}
