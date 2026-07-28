package com.hyperlofy.backend.finance.service;

import com.hyperlofy.backend.finance.entity.FinanceBudget;
import com.hyperlofy.backend.finance.entity.FinanceCostCentre;
import com.hyperlofy.backend.finance.entity.FinanceEntity;
import com.hyperlofy.backend.finance.entity.FinanceFinancialControl;
import com.hyperlofy.backend.finance.repository.FinanceBudgetRepository;
import com.hyperlofy.backend.finance.repository.FinanceCostCentreRepository;
import com.hyperlofy.backend.finance.repository.FinanceEntityRepository;
import com.hyperlofy.backend.finance.repository.FinanceFinancialControlRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(FinanceEnterpriseService.class);

    private final FinanceEntityRepository entityRepository;
    private final FinanceCostCentreRepository costCentreRepository;
    private final FinanceBudgetRepository budgetRepository;
    private final FinanceFinancialControlRepository controlRepository;

    @Transactional
    public FinanceEntity createLegalEntity(String entityCode, String entityName, String gstin) {
        log.info("[FINANCE ENTERPRISE] Registering Multi-Entity Legal Entity: Code={}, Name={}", entityCode, entityName);

        FinanceEntity entity = FinanceEntity.builder()
                .entityCode(entityCode)
                .entityName(entityName)
                .countryCode("IND")
                .taxRegistrationNumber(gstin)
                .currency("INR")
                .build();

        return entityRepository.save(entity);
    }

    @Transactional
    public FinanceBudget allocateDepartmentBudget(String budgetCode, String periodCode, BigDecimal allocatedAmount) {
        log.info("[FINANCE ENTERPRISE] Allocating Department Budget Code={}, Period={}, Amount={}",
                budgetCode, periodCode, allocatedAmount);

        FinanceBudget budget = FinanceBudget.builder()
                .budgetCode(budgetCode)
                .periodCode(periodCode)
                .allocatedAmount(allocatedAmount)
                .spentAmount(BigDecimal.ZERO)
                .status("APPROVED")
                .build();

        return budgetRepository.save(budget);
    }

    @Transactional(readOnly = true)
    public FinanceFinancialControl evaluateFinancialControl(String controlCode, BigDecimal transactionAmount) {
        log.info("[FINANCE ENTERPRISE] Evaluating financial control matrix for ControlCode={}, Amount={}", controlCode, transactionAmount);

        return controlRepository.findByControlCode(controlCode).orElseGet(() ->
                FinanceFinancialControl.builder()
                        .controlCode(controlCode)
                        .controlName("High-Value Financial Adjustment Control")
                        .thresholdAmount(new BigDecimal("50000.00"))
                        .requiresDualApproval(transactionAmount.compareTo(new BigDecimal("50000.00")) > 0)
                        .build()
        );
    }
}
