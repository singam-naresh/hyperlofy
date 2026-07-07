package com.hyperlofy.backend.payment.controller;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.payment.entity.Payment;
import com.hyperlofy.backend.payment.entity.Refund;
import com.hyperlofy.backend.payment.service.PaymentService;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<Payment> payOrder(@PathVariable UUID orderId) {
        Payment payment = paymentService.initiateWalletPayment(orderId);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PostMapping("/{paymentId}/refund-request")
    public ResponseEntity<Refund> submitRefundRequest(
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal amount,
            @RequestParam String reason) {
        Refund refund = paymentService.requestRefund(paymentId, amount, reason);
        return new ResponseEntity<>(refund, HttpStatus.CREATED);
    }

    @PostMapping("/admin/refunds/{refundId}/approve")
    public ResponseEntity<Refund> approveRefund(
            @PathVariable UUID refundId,
            @RequestParam String note) {
        User admin = getCurrentAuthenticatedUser();
        Refund refund = paymentService.approveRefund(refundId, admin.getId(), note);
        return ResponseEntity.ok(refund);
    }

    @PostMapping("/admin/refunds/{refundId}/reject")
    public ResponseEntity<Refund> rejectRefund(
            @PathVariable UUID refundId,
            @RequestParam String note) {
        User admin = getCurrentAuthenticatedUser();
        Refund refund = paymentService.rejectRefund(refundId, admin.getId(), note);
        return ResponseEntity.ok(refund);
    }

    @GetMapping("/admin/refunds/pending")
    public ResponseEntity<List<Refund>> getPendingRefundRequests() {
        return ResponseEntity.ok(paymentService.getPendingRefunds());
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.UNAUTHORIZED));
    }
}
