package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceBudgetRepository extends JpaRepository<FinanceBudget, UUID> {
    Optional<FinanceBudget> findByBudgetCode(String budgetCode);
}
