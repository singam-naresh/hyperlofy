package com.hyperlofy.backend.payments.controller;

import com.hyperlofy.backend.payments.entity.PaymentSubscription;
import com.hyperlofy.backend.payments.entity.PaymentToken;
import com.hyperlofy.backend.payments.service.PaymentEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments/enterprise")
@RequiredArgsConstructor
@Tag(name = "Payments Engine Enterprise Addendum API", description = "Endpoints for AI gateway selection, PCI-compliant card tokenization, recurring subscription mandates, and chargeback arbitration")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PaymentEnterpriseController {

    private final PaymentEnterpriseService enterpriseService;

    @GetMapping("/route")
    @Operation(summary = "Select Optimal Payment Gateway", description = "Returns top-priority payment gateway based on real-time success rate, cost, and latency metrics.")
    public ResponseEntity<Map<String, String>> routeGateway() {
        return ResponseEntity.ok(Map.of("selectedGateway", enterpriseService.selectOptimalGateway()));
    }

    @PostMapping("/tokens")
    @Operation(summary = "PCI Card Tokenization", description = "Stores encrypted card reference token for one-click checkout without persisting raw card numbers or CVVs.")
    public ResponseEntity<PaymentToken> tokenizeCard(
            @RequestParam UUID customerId,
            @RequestParam String providerName,
            @RequestParam String cardAlias,
            @RequestParam Integer expMonth,
            @RequestParam Integer expYear) {
        return ResponseEntity.ok(enterpriseService.tokenizeCardReference(customerId, providerName, cardAlias, expMonth, expYear));
    }

    @PostMapping("/subscriptions")
    @Operation(summary = "Create Recurring Payment Mandate", description = "Sets up recurring subscription plan with automated monthly billing schedules.")
    public ResponseEntity<PaymentSubscription> createSubscription(
            @RequestParam UUID customerId,
            @RequestParam String planName,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(enterpriseService.createSubscription(customerId, planName, amount));
    }

    @GetMapping("/tokens/{customerId}")
    @Operation(summary = "Get Customer Stored Payment Tokens", description = "Retrieves tokenized card references for customer wallet and checkout rendering.")
    public ResponseEntity<List<PaymentToken>> getTokens(@PathVariable UUID customerId) {
        return ResponseEntity.ok(enterpriseService.getCustomerTokens(customerId));
    }
}
