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
@RequestMapping("/api/v1/finance/customer")
@RequiredArgsConstructor
@Tag(name = "Finance Engine Customer API", description = "Endpoints for customers to view order tax invoices and download receipts")
@PreAuthorize("hasRole('CUSTOMER')")
public class FinanceCustomerController {

    private final FinanceBillingService financeService;

    @GetMapping("/{customerId}/invoices")
    @Operation(summary = "Get Customer Invoices", description = "Returns itemized tax invoices with CGST/SGST tax breakdown for all completed orders.")
    public ResponseEntity<List<FinanceInvoice>> getCustomerInvoices(@PathVariable UUID customerId) {
        return ResponseEntity.ok(financeService.getCustomerInvoices(customerId));
    }
}
