package com.hyperlofy.backend.payments.controller;

import com.hyperlofy.backend.payments.entity.Payment;
import com.hyperlofy.backend.payments.entity.PaymentRefund;
import com.hyperlofy.backend.payments.service.PaymentOrchestrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments/internal")
@RequiredArgsConstructor
@Tag(name = "Payments Engine Internal Integration API", description = "Endpoints for Unified Order Engine to initiate payment creation, authorization, capture, and refunds")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PaymentInternalController {

    private final PaymentOrchestrationService paymentService;

    @PostMapping("/create")
    @Operation(summary = "Create Order Payment Intent", description = "Initializes payment intent with gateway provider (Razorpay/Stripe).")
    public ResponseEntity<Payment> createPayment(
            @RequestParam UUID orderId,
            @RequestParam UUID customerId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String providerName,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(paymentService.createPayment(orderId, customerId, paymentMethod, providerName, amount));
    }

    @PostMapping("/authorize")
    @Operation(summary = "Authorize Payment Intent", description = "Holds funds on customer account for order fulfillment authorization.")
    public ResponseEntity<Payment> authorizePayment(@RequestParam UUID paymentId) {
        return ResponseEntity.ok(paymentService.authorizePayment(paymentId));
    }

    @PostMapping("/capture")
    @Operation(summary = "Capture Authorized Payment", description = "Captures pre-authorized funds upon order confirmation or delivery.")
    public ResponseEntity<Payment> capturePayment(@RequestParam UUID paymentId) {
        return ResponseEntity.ok(paymentService.capturePayment(paymentId));
    }

    @PostMapping("/refund")
    @Operation(summary = "Initiate Payment Refund", description = "Issues full or partial refund to customer via payment provider.")
    public ResponseEntity<PaymentRefund> refundPayment(
            @RequestParam UUID paymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        return ResponseEntity.ok(paymentService.initiateRefund(paymentId, refundAmount, reason));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get Payment Details", description = "Returns payment lifecycle state, transactions, and gateway IDs.")
    public ResponseEntity<Payment> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get Order Payment Details", description = "Fetches payment details associated with a master order ID.")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrder(orderId));
    }
}
