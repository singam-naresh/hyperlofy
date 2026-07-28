package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.RefundCase;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
@Tag(name = "Customer Refund Operations API", description = "Process full/partial refunds, wallet credits, original payment method reversals, and instant bank transfers")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class RefundOperationController {

    private final CustomerSupportService supportService;

    @PostMapping
    @Operation(summary = "Request Customer Refund", description = "Initiates full or partial refund to Customer Wallet or Original Payment Method.")
    public ResponseEntity<RefundCase> requestRefund(
            @RequestParam String refundCode,
            @RequestParam UUID ticketId,
            @RequestParam UUID orderId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(supportService.requestRefund(refundCode, ticketId, orderId, amount, reason, method, tenantId));
    }
}
