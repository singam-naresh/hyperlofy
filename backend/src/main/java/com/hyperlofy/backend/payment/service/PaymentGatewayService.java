package com.hyperlofy.backend.payment.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.ledger.service.LedgerService;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.payment.entity.*;
import com.hyperlofy.backend.payment.repository.PaymentAuditRepository;
import com.hyperlofy.backend.payment.repository.PaymentEventRepository;
import com.hyperlofy.backend.payment.repository.PaymentRepository;
import com.hyperlofy.backend.payment.repository.RefundAuditRepository;
import com.hyperlofy.backend.payment.repository.RefundRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentAuditRepository paymentAuditRepository;
    private final RefundRepository refundRepository;
    private final RefundAuditRepository refundAuditRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;
    private final LedgerService ledgerService;

    /**
     * Creates a verified transaction order on Razorpay servers and maps to a local payment log.
     */
    @Transactional
    public Payment createGatewayOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        // 1. Idempotency Check: Prevent duplicate gateway order initialization
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
        if (existingPayment.isPresent()) {
            Payment p = existingPayment.get();
            if (p.getPaymentStatus() == PaymentStatus.COMPLETED) {
                throw new BusinessException("Payment is already completed for this order.", HttpStatus.BAD_REQUEST);
            }
            log.info("Returning existing pending payment gateway order for order ID: {}", orderId);
            return p;
        }

        BigDecimal deliveryFee = order.getDeliveryFee();

        // 2. Call Razorpay API
        String gatewayOrderId = razorpayService.createRazorpayOrder(deliveryFee, orderId.toString());

        // 3. Persist local checkout intent
        Payment payment = Payment.builder()
                .order(order)
                .amount(deliveryFee)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentGateway(PaymentGateway.RAZORPAY)
                .gatewayOrderId(gatewayOrderId)
                .build();

        payment = paymentRepository.save(payment);

        // 4. Record audit trace
        logPaymentAudit(payment.getId(), "CREATE_GATEWAY_ORDER", "SUCCESS", "Razorpay order created with ID: " + gatewayOrderId);

        log.info("Initialized Razorpay gateway payment sequence of {} for order ID: {}", deliveryFee, orderId);
        return payment;
    }

    /**
     * Verifies payment signature and processes Order transition & Escrow locks.
     */
    @Transactional
    public Payment verifyAndCompletePayment(UUID paymentId, String gatewayPaymentId, String signature) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment record not found", HttpStatus.NOT_FOUND));

        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            log.info("Payment with ID: {} has already been captured and processed.", paymentId);
            return payment;
        }

        // 1. Verify signatures matching order, payment and checksum secret
        boolean isValid = razorpayService.verifySignature(payment.getGatewayOrderId(), gatewayPaymentId, signature);
        if (!isValid) {
            logPaymentAudit(paymentId, "VERIFY_SIGNATURE", "FAILED", "Signature hash mismatch. Checksum validation failed.");
            throw new BusinessException("Payment validation signature hash is invalid.", HttpStatus.BAD_REQUEST);
        }

        // 2. Transition payment stats
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(gatewayPaymentId);
        payment = paymentRepository.save(payment);

        // 3. Mark corresponding Order as PAID
        Order order = payment.getOrder();
        order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
        orderRepository.save(order);

        // 4. Secure funds in double-entry Escrow Pool
        ledgerService.placeInEscrow(order.getId(), payment.getId(), payment.getAmount());

        // 5. Save audit records
        logPaymentAudit(payment.getId(), "CAPTURE_PAYMENT", "COMPLETED", "Signature verified. Funds verified and placed in Escrow pool.");

        log.info("Transaction validated and captured. Order status set to PAYMENT_SUCCESS, Escrow locked for Order ID: {}", order.getId());
        return payment;
    }

    /**
     * Programmatic admin approval triggers double-entry Ledger refund flows.
     */
    @Transactional
    public Refund approveAndProcessRefund(UUID refundId, UUID adminUserId, String note) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new BusinessException("Refund record not found", HttpStatus.NOT_FOUND));

        if (refund.getRefundStatus() != RefundStatus.PENDING_APPROVAL) {
            throw new BusinessException("Refund is already processed/rejected.", HttpStatus.BAD_REQUEST);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BusinessException("Admin user not found", HttpStatus.NOT_FOUND));

        Payment payment = refund.getPayment();
        Order order = payment.getOrder();

        // 1. Double-Entry ledger refund routing
        ledgerService.refundEscrow(order.getId());

        // 2. Complete refund state transitions
        refund.setRefundStatus(RefundStatus.PROCESSED);
        refund.setApprovedBy(admin);
        refund.setApprovalNote(note);
        refund = refundRepository.save(refund);

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        logRefundAudit(refundId, "APPROVE_REFUND", "PROCESSED", "Admin " + admin.getEmail() + " approved refund. Funds released back from Escrow ledger.");

        log.info("Refund request approved and ledger transactions processed. Order ID: {}, Refund ID: {}", order.getId(), refundId);
        return refund;
    }

    /**
     * Webhook Processor handling event-driven callbacks.
     */
    @Transactional
    public void processWebhookEvent(String eventType, String payload) {
        log.info("Received Razorpay Webhook Event callback: {}", eventType);

        // Prevent processing logs from completing twice
        boolean exists = paymentEventRepository.findAll().stream()
                .anyMatch(pe -> pe.getEventType().equalsIgnoreCase(eventType) && pe.getPayload().contains(payload));
        if (exists) {
            log.info("Duplicate Webhook Event Ignored (Replay protection): type: {}", eventType);
            return;
        }

        PaymentEvent we = PaymentEvent.builder()
                .eventType(eventType)
                .payload(payload)
                .processed(true)
                .build();
        paymentEventRepository.save(we);

        // Core Event Handlers
        switch (eventType) {
            case "payment.captured":
                log.info("Webhook: Payment captured event processed.");
                break;
            case "payment.failed":
                log.info("Webhook: Payment failed event logged.");
                break;
            case "refund.created":
                log.info("Webhook: Refund created event registered.");
                break;
            case "refund.processed":
                log.info("Webhook: Refund processed event reconciled.");
                break;
            default:
                log.warn("Webhook: Unknown event logged: {}", eventType);
                break;
        }
    }

    private void logPaymentAudit(UUID paymentId, String action, String status, String details) {
        PaymentAudit audit = PaymentAudit.builder()
                .paymentId(paymentId)
                .actionType(action)
                .status(status)
                .details(details)
                .build();
        paymentAuditRepository.save(audit);
    }

    private void logRefundAudit(UUID refundId, String action, String status, String details) {
        RefundAudit audit = RefundAudit.builder()
                .refundId(refundId)
                .actionType(action)
                .status(status)
                .details(details)
                .build();
        refundAuditRepository.save(audit);
    }
}
