package com.hyperlofy.backend.buyforme.controller;

import com.hyperlofy.backend.buyforme.entity.BuyForMeBudgetHistory;
import com.hyperlofy.backend.buyforme.entity.BuyForMeExpense;
import com.hyperlofy.backend.buyforme.entity.BuyForMeSubstitution;
import com.hyperlofy.backend.buyforme.service.BuyForMeEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buy-for-me/enterprise")
@RequiredArgsConstructor
@Tag(name = "Buy For Me Enterprise Addendum API", description = "Endpoints for budget variance requests, item substitutions, and driver expense reimbursements")
public class BuyForMeEnterpriseController {

    private final BuyForMeEnterpriseService enterpriseService;

    @PostMapping("/orders/{orderId}/budget-increase")
    @PreAuthorize("hasAnyRole('DELIVERY_PARTNER', 'ADMIN')")
    @Operation(summary = "Request Budget Increase", description = "Requests a budget increase when store pricing exceeds original customer budget estimate.")
    public ResponseEntity<BuyForMeBudgetHistory> requestBudgetIncrease(
            @PathVariable UUID orderId,
            @RequestParam Double newBudget,
            @RequestParam String reason) {
        return ResponseEntity.ok(enterpriseService.requestBudgetIncrease(orderId, newBudget, reason, "DRIVER"));
    }

    @PostMapping("/orders/{orderId}/substitutions")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "Suggest Item Substitution", description = "Delivery partner suggests an alternative brand, size, or item when original product is out of stock.")
    public ResponseEntity<BuyForMeSubstitution> suggestSubstitution(
            @PathVariable UUID orderId,
            @RequestParam String origItem,
            @RequestParam String subItem,
            @RequestParam(required = false) String subBrand,
            @RequestParam Double subPrice) {
        return ResponseEntity.ok(enterpriseService.suggestSubstitution(orderId, origItem, subItem, subBrand, subPrice, "DRIVER"));
    }

    @PostMapping("/orders/{orderId}/expenses")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    @Operation(summary = "Submit Driver Expense", description = "Submits driver personal spend, platform advance, or parking fee for reimbursement approval.")
    public ResponseEntity<BuyForMeExpense> submitExpense(
            @PathVariable UUID orderId,
            @RequestParam UUID driverId,
            @RequestParam String expenseType,
            @RequestParam Double amount,
            @RequestParam(required = false) String receiptUrl) {
        return ResponseEntity.ok(enterpriseService.submitDriverExpense(orderId, driverId, expenseType, amount, receiptUrl));
    }
}
