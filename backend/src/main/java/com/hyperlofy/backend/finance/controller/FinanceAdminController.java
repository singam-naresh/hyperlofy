package com.hyperlofy.backend.finance.controller;

import com.hyperlofy.backend.finance.entity.FinanceAccountingPeriod;
import com.hyperlofy.backend.finance.service.FinanceBillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance/admin")
@RequiredArgsConstructor
@Tag(name = "Finance Engine Admin API", description = "Endpoints for finance operations to close accounting periods and inspect revenue recognition ledgers")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class FinanceAdminController {

    private final FinanceBillingService financeService;

    @PostMapping("/close-period")
    @Operation(summary = "Close Accounting Period", description = "Performs month-end or quarter-end financial close and locks accounting entries.")
    public ResponseEntity<FinanceAccountingPeriod> closePeriod(
            @RequestParam String periodCode,
            @RequestParam String closedBy) {
        return ResponseEntity.ok(financeService.closeAccountingPeriod(periodCode, closedBy));
    }
}
