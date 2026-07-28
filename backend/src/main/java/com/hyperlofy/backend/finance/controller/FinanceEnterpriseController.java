package com.hyperlofy.backend.finance.controller;

import com.hyperlofy.backend.finance.entity.FinanceBudget;
import com.hyperlofy.backend.finance.entity.FinanceEntity;
import com.hyperlofy.backend.finance.entity.FinanceFinancialControl;
import com.hyperlofy.backend.finance.service.FinanceEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/finance/enterprise")
@RequiredArgsConstructor
@Tag(name = "Finance Engine Enterprise Addendum API", description = "Endpoints for Multi-Entity Accounting, Budgeting & Forecasting, Financial Controls Dual Approval, and Executive KPI Dashboards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class FinanceEnterpriseController {

    private final FinanceEnterpriseService enterpriseService;

    @PostMapping("/entity/register")
    @Operation(summary = "Register Legal Operating Entity", description = "Registers multi-entity corporate subsidiary with GSTIN tax registration.")
    public ResponseEntity<FinanceEntity> registerEntity(
            @RequestParam String entityCode,
            @RequestParam String entityName,
            @RequestParam String gstin) {
        return ResponseEntity.ok(enterpriseService.createLegalEntity(entityCode, entityName, gstin));
    }

    @PostMapping("/budget/allocate")
    @Operation(summary = "Allocate Department Budget", description = "Allocates annual or quarterly departmental operating budget.")
    public ResponseEntity<FinanceBudget> allocateBudget(
            @RequestParam String budgetCode,
            @RequestParam String periodCode,
            @RequestParam BigDecimal allocatedAmount) {
        return ResponseEntity.ok(enterpriseService.allocateDepartmentBudget(budgetCode, periodCode, allocatedAmount));
    }

    @GetMapping("/control/evaluate")
    @Operation(summary = "Evaluate Financial Control Threshold", description = "Evaluates transaction amount against financial control policies for dual approval requirement.")
    public ResponseEntity<FinanceFinancialControl> evaluateControl(
            @RequestParam String controlCode,
            @RequestParam BigDecimal transactionAmount) {
        return ResponseEntity.ok(enterpriseService.evaluateFinancialControl(controlCode, transactionAmount));
    }
}
