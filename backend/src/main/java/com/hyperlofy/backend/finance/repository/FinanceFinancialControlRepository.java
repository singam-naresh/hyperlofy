package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceFinancialControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceFinancialControlRepository extends JpaRepository<FinanceFinancialControl, UUID> {
    Optional<FinanceFinancialControl> findByControlCode(String controlCode);
}
