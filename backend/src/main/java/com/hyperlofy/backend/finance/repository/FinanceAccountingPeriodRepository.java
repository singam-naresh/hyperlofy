package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceAccountingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceAccountingPeriodRepository extends JpaRepository<FinanceAccountingPeriod, UUID> {
    Optional<FinanceAccountingPeriod> findByPeriodCode(String periodCode);
}
