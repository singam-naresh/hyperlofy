package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceCostCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceCostCentreRepository extends JpaRepository<FinanceCostCentre, UUID> {
    Optional<FinanceCostCentre> findByCostCentreCode(String costCentreCode);
}
