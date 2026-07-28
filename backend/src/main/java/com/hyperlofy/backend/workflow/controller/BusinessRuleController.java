package com.hyperlofy.backend.workflow.controller;

import com.hyperlofy.backend.workflow.entity.BusinessRule;
import com.hyperlofy.backend.workflow.service.WorkflowBpmEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
@Tag(name = "Business Rules Engine API", description = "Configure and evaluate DMN-inspired business decision rules — refund approval thresholds, merchant rating rules, delivery assignment rules — without changing Java code")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class BusinessRuleController {

    private final WorkflowBpmEnterpriseService bpmEnterpriseService;

    @PostMapping
    @Operation(summary = "Register Business Rule",
            description = "Registers a DMN-style business rule — e.g. Refund < ₹500 = AUTO_APPROVE, Refund ₹500–₹5000 = REQUIRE_REVIEW (FINANCE_TEAM). Rules are evaluated in priority order.")
    public ResponseEntity<BusinessRule> registerRule(
            @RequestParam String ruleKey,
            @RequestParam String ruleName,
            @RequestParam String ruleCategory,
            @RequestParam String conditionField,
            @RequestParam String conditionOperator,
            @RequestParam(required = false) BigDecimal conditionValueMin,
            @RequestParam(required = false) BigDecimal conditionValueMax,
            @RequestParam String actionType,
            @RequestParam(required = false) String actionValue,
            @RequestParam(required = false) Integer priority) {
        return ResponseEntity.ok(bpmEnterpriseService.registerRule(
                ruleKey, ruleName, ruleCategory, conditionField, conditionOperator,
                conditionValueMin, conditionValueMax, actionType, actionValue, priority));
    }

    @GetMapping
    @Operation(summary = "List All Business Rules",
            description = "Returns all registered DMN decision rules with their conditions, actions, and active status.")
    public ResponseEntity<List<BusinessRule>> listRules() {
        return ResponseEntity.ok(bpmEnterpriseService.getAllRules());
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate Business Rule",
            description = "Evaluates the first matching rule in a category against an input value. Returns the matched rule with its action (AUTO_APPROVE, REQUIRE_REVIEW, ESCALATE, REJECT).")
    public ResponseEntity<BusinessRule> evaluateRule(
            @RequestParam String ruleCategory,
            @RequestParam BigDecimal inputValue) {
        BusinessRule matched = bpmEnterpriseService.evaluateRule(ruleCategory, inputValue);
        return matched != null ? ResponseEntity.ok(matched) : ResponseEntity.noContent().build();
    }
}
