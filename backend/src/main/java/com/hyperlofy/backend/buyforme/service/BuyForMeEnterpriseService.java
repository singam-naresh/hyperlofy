package com.hyperlofy.backend.buyforme.service;

import com.hyperlofy.backend.buyforme.entity.BuyForMeBudgetHistory;
import com.hyperlofy.backend.buyforme.entity.BuyForMeExpense;
import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import com.hyperlofy.backend.buyforme.entity.BuyForMeSubstitution;
import com.hyperlofy.backend.buyforme.repository.BuyForMeBudgetHistoryRepository;
import com.hyperlofy.backend.buyforme.repository.BuyForMeExpenseRepository;
import com.hyperlofy.backend.buyforme.repository.BuyForMeOrderRepository;
import com.hyperlofy.backend.buyforme.repository.BuyForMeSubstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyForMeEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(BuyForMeEnterpriseService.class);

    private final BuyForMeOrderRepository orderRepository;
    private final BuyForMeBudgetHistoryRepository budgetRepository;
    private final BuyForMeSubstitutionRepository substitutionRepository;
    private final BuyForMeExpenseRepository expenseRepository;

    @Transactional
    public BuyForMeBudgetHistory requestBudgetIncrease(UUID orderId, Double newBudget, String reason, String actor) {
        BuyForMeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("BuyForMeOrder not found: " + orderId));

        double orig = order.getMaxBudget();
        double variance = ((newBudget - orig) / orig) * 100.0;
        log.warn("[BUY FOR ME ENTERPRISE] Budget Increase Requested OrderId={}, Orig={}, New={}, Variance={}%", orderId, orig, newBudget, String.format("%.2f", variance));

        BuyForMeBudgetHistory bh = BuyForMeBudgetHistory.builder()
                .orderId(orderId)
                .originalBudget(orig)
                .requestedBudget(newBudget)
                .variancePercentage(variance)
                .status("REQUESTED")
                .reason(reason)
                .requestedBy(actor)
                .build();

        return budgetRepository.save(bh);
    }

    @Transactional
    public BuyForMeSubstitution suggestSubstitution(UUID orderId, String origItem, String subItem, String subBrand, Double subPrice, String suggestedBy) {
        log.info("[BUY FOR ME ENTERPRISE] Item Substitution Suggested OrderId={}, Orig={}, Sub={}, Price={}", orderId, origItem, subItem, subPrice);

        BuyForMeSubstitution sub = BuyForMeSubstitution.builder()
                .orderId(orderId)
                .originalItemName(origItem)
                .substituteItemName(subItem)
                .substituteBrand(subBrand)
                .substitutePrice(subPrice)
                .suggestedBy(suggestedBy)
                .status("PENDING")
                .build();

        return substitutionRepository.save(sub);
    }

    @Transactional
    public BuyForMeExpense submitDriverExpense(UUID orderId, UUID driverId, String expenseType, Double amount, String receiptUrl) {
        log.info("[BUY FOR ME ENTERPRISE] Driver Expense Submitted OrderId={}, DriverId={}, Type={}, Amount={}", orderId, driverId, expenseType, amount);

        BuyForMeExpense expense = BuyForMeExpense.builder()
                .orderId(orderId)
                .driverId(driverId)
                .expenseType(expenseType)
                .amount(amount)
                .receiptUrl(receiptUrl)
                .status("SUBMITTED")
                .build();

        return expenseRepository.save(expense);
    }
}
