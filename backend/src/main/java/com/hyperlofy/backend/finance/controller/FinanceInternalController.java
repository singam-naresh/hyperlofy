package com.hyperlofy.backend.finance.controller;

import com.hyperlofy.backend.finance.entity.FinanceCreditNote;
import com.hyperlofy.backend.finance.entity.FinanceInvoice;
import com.hyperlofy.backend.finance.service.FinanceBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/internal")
@RequiredArgsConstructor
@Tag(name = "Finance Engine Internal Integration API", description = "Endpoints for Unified Order Engine & Payments Engine to generate GST tax invoices, credit notes, and accounting ledger entries")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class FinanceInternalController {

    private final FinanceBillingService financeService;

    @PostMapping("/invoice")
    @Operation(summary = "Generate Order GST Tax Invoice", description = "Calculates CGST, SGST, IGST tax breakdown and generates official customer tax invoice.")
    public ResponseEntity<FinanceInvoice> generateInvoice(
            @RequestParam UUID orderId,
            @RequestParam UUID customerId,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam BigDecimal grossAmount,
            @RequestParam(required = false) BigDecimal discountAmount) {
        return ResponseEntity.ok(financeService.generateCustomerInvoice(orderId, customerId, merchantId, grossAmount, discountAmount));
    }

    @PostMapping("/credit-note")
    @Operation(summary = "Issue Refund Credit Note", description = "Generates official tax credit note against an existing invoice for customer refunds.")
    public ResponseEntity<FinanceCreditNote> issueCreditNote(
            @RequestParam UUID invoiceId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason) {
        return ResponseEntity.ok(financeService.issueCreditNote(invoiceId, refundAmount, reason));
    }
}
