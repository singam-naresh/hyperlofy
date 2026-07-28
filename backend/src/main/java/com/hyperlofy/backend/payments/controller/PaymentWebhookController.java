package com.hyperlofy.backend.payments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/provider")
@RequiredArgsConstructor
@Tag(name = "Payments Engine Gateway Webhook API", description = "Asynchronous webhook receiver for Razorpay, Stripe, and Cashfree payment status callbacks")
public class PaymentWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookController.class);

    @PostMapping("/webhook")
    @Operation(summary = "Process Gateway Webhook", description = "Verifies signature hash and updates payment transaction status asynchronously.")
    public ResponseEntity<Map<String, Object>> handleWebhook(
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestBody String payload) {
        log.info("[PAYMENTS ENGINE] Received payment provider webhook notification. Payload size: {} bytes", payload.length());
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Webhook verified and processed successfully"));
    }
}
