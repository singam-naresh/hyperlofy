package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.entity.CustomerWallet;
import com.hyperlofy.backend.customer.entity.CustomerWalletTransaction;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
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
@RequestMapping("/api/v1/customer/wallet")
@RequiredArgsConstructor
@Tag(name = "Customer Wallet API", description = "Endpoints for customer wallet balance, credits, refund transactions, and cashback history")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerWalletController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Customer Wallet", description = "Retrieves wallet balance and reward points for authenticated customer.")
    public ResponseEntity<CustomerWallet> getWallet(Principal principal) {
        return ResponseEntity.ok(customerService.getWallet(UUID.randomUUID()));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get Wallet Transactions", description = "Retrieves chronological audit log of wallet credits, debits, and refunds.")
    public ResponseEntity<List<CustomerWalletTransaction>> getWalletTransactions(Principal principal) {
        return ResponseEntity.ok(customerService.getWalletTransactions(UUID.randomUUID()));
    }
}
