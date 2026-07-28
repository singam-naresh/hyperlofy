package com.hyperlofy.backend.payments.service;

import com.hyperlofy.backend.payments.entity.Payment;
import com.hyperlofy.backend.payments.entity.PaymentRefund;
import com.hyperlofy.backend.payments.entity.PaymentTransaction;
import com.hyperlofy.backend.payments.repository.PaymentRefundRepository;
import com.hyperlofy.backend.payments.repository.PaymentRepository;
import com.hyperlofy.backend.payments.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(PaymentOrchestrationService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentRefundRepository refundRepository;

    @Transactional
    public Payment createPayment(UUID orderId, UUID customerId, String paymentMethod, String providerName, BigDecimal amount) {
        log.info("[PAYMENTS ENGINE] Creating payment OrderId={}, Method={}, Provider={}, Amount={}", orderId, paymentMethod, providerName, amount);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .customerId(customerId)
                .paymentMethod(paymentMethod)
                .providerName(providerName != null ? providerName : "RAZORPAY")
                .providerPaymentId("pay_" + UUID.randomUUID().toString().substring(0, 12))
                .amount(amount)
                .currency("INR")
                .status("PAYMENT_CREATED")
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment authorizePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        log.info("[PAYMENTS ENGINE] Authorizing payment PaymentId={}, Amount={}", paymentId, payment.getAmount());
        payment.setStatus("AUTHORIZED");

        PaymentTransaction tx = PaymentTransaction.builder()
                .paymentId(paymentId)
                .transactionType("AUTHORIZE")
                .amount(payment.getAmount())
                .status("SUCCESS")
                .providerTransactionId("tx_auth_" + UUID.randomUUID().toString().substring(0, 10))
                .build();

        transactionRepository.save(tx);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment capturePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        log.info("[PAYMENTS ENGINE] Capturing payment PaymentId={}, Amount={}", paymentId, payment.getAmount());
        payment.setStatus("CAPTURED");

        PaymentTransaction tx = PaymentTransaction.builder()
                .paymentId(paymentId)
                .transactionType("CAPTURE")
                .amount(payment.getAmount())
                .status("SUCCESS")
                .providerTransactionId("tx_cap_" + UUID.randomUUID().toString().substring(0, 10))
                .build();

        transactionRepository.save(tx);
        return paymentRepository.save(payment);
    }

    @Transactional
    public PaymentRefund initiateRefund(UUID paymentId, BigDecimal refundAmount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        log.info("[PAYMENTS ENGINE] Initiating refund PaymentId={}, RefundAmount={}, Reason={}", paymentId, refundAmount, reason);
        payment.setStatus("REFUNDED");
        paymentRepository.save(payment);

        PaymentRefund refund = PaymentRefund.builder()
                .paymentId(paymentId)
                .refundAmount(refundAmount)
                .reason(reason)
                .status("REFUNDED")
                .providerRefundId("rfnd_" + UUID.randomUUID().toString().substring(0, 10))
                .build();

        PaymentTransaction tx = PaymentTransaction.builder()
                .paymentId(paymentId)
                .transactionType("REFUND")
                .amount(refundAmount)
                .status("SUCCESS")
                .providerTransactionId(refund.getProviderRefundId())
                .build();

        transactionRepository.save(tx);
        return refundRepository.save(refund);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }

    @Transactional(readOnly = true)
    public Payment getPaymentByOrder(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for order: " + orderId));
    }
}
