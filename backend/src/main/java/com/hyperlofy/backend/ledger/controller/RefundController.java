package com.hyperlofy.backend.ledger.controller;

import com.hyperlofy.backend.ledger.dto.RefundRequestDTO;
import com.hyperlofy.backend.ledger.dto.RefundResponseDTO;
import com.hyperlofy.backend.ledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
@Tag(name = "Refund Reconciliation", description = "Endpoints for refund and settlement reconciliation processing")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class RefundController {

    private final LedgerService ledgerService;

    @PostMapping("/reconcile/{orderId}")
    @Operation(summary = "Reconcile order refund", description = "Executes full or partial refund reconciliation across escrow, platform, agent, and merchant ledgers with idempotency guarantees.")
    public ResponseEntity<RefundResponseDTO> reconcileRefund(
            @PathVariable UUID orderId,
            @Valid @RequestBody(required = false) RefundRequestDTO request) {

        BigDecimal refundAmount = (request != null) ? request.getRefundAmount() : null;
        String reason = (request != null && request.getReason() != null) ? request.getReason() : "Customer refund requested";

        RefundResponseDTO response = ledgerService.processRefundReconciliation(orderId, refundAmount, reason);
        return ResponseEntity.ok(response);
    }
}
