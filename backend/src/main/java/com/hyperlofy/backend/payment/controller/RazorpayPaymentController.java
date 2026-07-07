package com.hyperlofy.backend.payment.controller;

import com.hyperlofy.backend.payment.entity.Payment;
import com.hyperlofy.backend.payment.service.PaymentGatewayService;
import com.hyperlofy.backend.payment.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments/razorpay")
@RequiredArgsConstructor
public class RazorpayPaymentController {

    private final PaymentGatewayService paymentGatewayService;
    private final RazorpayService razorpayService;

    /**
     * Creates a Programmatic Razorpay order for local Order checkout.
     */
    @PostMapping("/create/{orderId}")
    public ResponseEntity<Payment> createRazorpayOrder(@PathVariable UUID orderId) {
        log.info("REST: Receive Razorpay checkout initialization request for order: {}", orderId);
        Payment payment = paymentGatewayService.createGatewayOrder(orderId);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    /**
     * Captures and completes order checkout after validating payment signatures.
     */
    @PostMapping("/complete")
    public ResponseEntity<Payment> completePayment(
            @RequestParam UUID paymentId,
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpaySignature) {
        log.info("REST: Receive verify & capture checkout signal for payment ID: {}", paymentId);
        Payment payment = paymentGatewayService.verifyAndCompletePayment(paymentId, razorpayPaymentId, razorpaySignature);
        return ResponseEntity.ok(payment);
    }

    /**
     * Receives event-driven callbacks from Razorpay webhook dispatchers.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String body) {
        log.info("REST: Receive Razorpay Callback signal hook.");

        // 1. Verify webhook signature
        boolean isValid = razorpayService.verifyWebhookSignature(body, signature);
        if (!isValid) {
            log.warn("Webhook Signature verification FAILED! Rejecting callback.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 2. Extract event and process asynchronously or synchronously
        try {
            org.json.JSONObject webhookJson = new org.json.JSONObject(body);
            String event = webhookJson.optString("event");
            paymentGatewayService.processWebhookEvent(event, body);
        } catch (Exception e) {
            log.error("Failed to parse callback payload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // Return a successful acknowledgment
        return ResponseEntity.ok().build();
    }
}
