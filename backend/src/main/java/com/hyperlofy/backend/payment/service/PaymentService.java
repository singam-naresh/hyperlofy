package com.hyperlofy.backend.payment.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.dto.OrderStatusUpdateRequest;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.order.service.AssignmentService;
import com.hyperlofy.backend.order.service.OrderService;
import com.hyperlofy.backend.payment.entity.*;
import com.hyperlofy.backend.payment.repository.PaymentRepository;
import com.hyperlofy.backend.payment.repository.RefundRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.wallet.entity.TransactionType;
import com.hyperlofy.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final AssignmentService assignmentService;
    private final UserRepository userRepository;
    private final WalletService walletService;

    @Transactional
    public Payment initiateWalletPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new BusinessException("Payment already exists or initiated for order", HttpStatus.BAD_REQUEST);
        }

        BigDecimal fee = order.getDeliveryFee();
        UUID customerId = order.getCustomer().getId();

        // Debit the wallet directly (pessimistic locked behind scenes)
        walletService.debitWallet(
                customerId,
                fee,
                TransactionType.ORDER_PAYMENT,
                orderId,
                "Delivery fee payment for Order: " + order.getStoreName()
        );

        orderService.updateOrderStatus(orderId, OrderStatusUpdateRequest.builder()
                .nextStatus(OrderStatus.PAYMENT_PENDING)
                .build());
        orderService.updateOrderStatus(orderId, OrderStatusUpdateRequest.builder()
                .nextStatus(OrderStatus.PAYMENT_SUCCESS)
                .build());

        // Automatically assign nearby available agent after successful payment
        assignmentService.assignAgentToOrder(orderId);

        Payment payment = Payment.builder()
                .order(order)
                .amount(fee)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentGateway(PaymentGateway.WALLET)
                .transactionId(UUID.randomUUID().toString())
                .build();

        log.info("Successfully completed wallet payment of {} for order {}", fee, orderId);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Refund requestRefund(UUID paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException("Payment details log entry not found", HttpStatus.NOT_FOUND));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("Cannot refund a payment that is not marked COMPLETED", HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("Refund amount cannot exceed original payment amount", HttpStatus.BAD_REQUEST);
        }

        Refund refund = Refund.builder()
                .payment(payment)
                .amount(amount)
                .refundStatus(RefundStatus.PENDING_APPROVAL)
                .reason(reason)
                .build();

        log.info("Refund request initiated and submitted for approval. Amount: {}, Payment: {}", amount, paymentId);
        return refundRepository.save(refund);
    }

    @Transactional
    public Refund approveRefund(UUID refundId, UUID adminUserId, String note) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new BusinessException("Refund process log not found", HttpStatus.NOT_FOUND));

        if (refund.getRefundStatus() != RefundStatus.PENDING_APPROVAL) {
            throw new BusinessException("Refund request is not in PENDING_APPROVAL status", HttpStatus.BAD_REQUEST);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BusinessException("Admin user not found", HttpStatus.NOT_FOUND));

        Payment payment = refund.getPayment();

        // Proceed to refund funds back if payment gateway was WALLET
        if (payment.getPaymentGateway() == PaymentGateway.WALLET) {
            walletService.creditWallet(
                    payment.getOrder().getCustomer().getId(),
                    refund.getAmount(),
                    TransactionType.REFUND,
                    payment.getOrder().getId(),
                    "Refund approved: " + refund.getReason()
            );
        } else {
            log.info("Refunding back to dynamic Gateway source IDs: {}", payment.getTransactionId());
        }

        refund.setRefundStatus(RefundStatus.PROCESSED);
        refund.setApprovedBy(admin);
        refund.setApprovalNote(note);

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Refund ID [{}] approved and processed by Admin [{}]. Status set to PROCESSED.", refundId, admin.getEmail());
        return refundRepository.save(refund);
    }

    @Transactional
    public Refund rejectRefund(UUID refundId, UUID adminUserId, String note) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new BusinessException("Refund audit record not found", HttpStatus.NOT_FOUND));

        if (refund.getRefundStatus() != RefundStatus.PENDING_APPROVAL) {
            throw new BusinessException("Refund request is not in PENDING_APPROVAL status", HttpStatus.BAD_REQUEST);
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new BusinessException("Admin user not found", HttpStatus.NOT_FOUND));

        refund.setRefundStatus(RefundStatus.REJECTED);
        refund.setApprovedBy(admin);
        refund.setApprovalNote(note);

        log.info("Refund [{}] rejected by Admin [{}]. Note: {}", refundId, admin.getEmail(), note);
        return refundRepository.save(refund);
    }

    @Transactional(readOnly = true)
    public List<Refund> getPendingRefunds() {
        return refundRepository.findByRefundStatus(RefundStatus.PENDING_APPROVAL);
    }
}
