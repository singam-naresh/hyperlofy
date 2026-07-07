package com.hyperlofy.backend.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class RazorpayService {

    private final String keyId;
    private final String keySecret;
    private final String webhookSecret;

    public RazorpayService(
            @Value("${app.razorpay.key-id}") String keyId,
            @Value("${app.razorpay.key-secret}") String keySecret) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        // In production, configure webhook secret separately. Using keySecret as fallback.
        this.webhookSecret = keySecret;
    }

    /**
     * Creates a verified transaction order on Razorpay servers.
     */
    public String createRazorpayOrder(BigDecimal amount, String receiptId) {
        try {
            RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);
            
            JSONObject orderRequest = new JSONObject();
            // Convert to Paise (1 INR = 100 Paise)
            int amountInPaise = amount.multiply(new BigDecimal("100")).intValue();
            
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);
            orderRequest.put("payment_capture", 1); // Auto capture payment

            Order order = razorpay.orders.create(orderRequest);
            String rzpOrderId = order.get("id");
            log.info("Successfully created Razorpay Order. Local Receipt: {}, Gateway Order ID: {}", receiptId, rzpOrderId);
            return rzpOrderId;
        } catch (Exception e) {
            log.error("Fatal error creating Razorpay Order", e);
            throw new RuntimeException("Razorpay order creation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies signature authenticity matching razorpay_order_id | razorpay_payment_id with signature
     */
    public boolean verifySignature(String rzpOrderId, String rzpPaymentId, String signature) {
        try {
            String payload = rzpOrderId + "|" + rzpPaymentId;
            return verifyHmacSha256(payload, signature, keySecret);
        } catch (Exception e) {
            log.error("Signature verification error", e);
            return false;
        }
    }

    /**
     * Verifies body payload signatures on Webhook callbacks
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        return verifyHmacSha256(payload, signature, webhookSecret);
    }

    private boolean verifyHmacSha256(String payload, String signature, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equalsIgnoreCase(signature);
        } catch (Exception e) {
            log.error("HMAC SHA256 computation failure", e);
            return false;
        }
    }
}
