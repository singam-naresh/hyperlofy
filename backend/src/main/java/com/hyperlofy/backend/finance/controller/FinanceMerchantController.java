package com.hyperlofy.backend.finance.controller;

import com.hyperlofy.backend.finance.entity.FinanceInvoice;
import com.hyperlofy.backend.finance.service.FinanceBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/finance/merchant")
@RequiredArgsConstructor
@Tag(name = "Finance Engine Merchant API", description = "Endpoints for merchants to access platform commission tax invoices and monthly billing statements")
@PreAuthorize("hasRole('MERCHANT')")
public class FinanceMerchantController {

    private final FinanceBillingService financeService;

    @GetMapping("/{merchantId}/invoices")
    @Operation(summary = "Get Merchant Invoices & Tax Statements", description = "Returns platform commission tax invoices and GST tax credit breakdown.")
    public ResponseEntity<List<FinanceInvoice>> getMerchantInvoices(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(financeService.getMerchantInvoices(merchantId));
    }
}
