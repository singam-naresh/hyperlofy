package com.hyperlofy.backend.governance.repository;

import com.hyperlofy.backend.governance.entity.TechnicalDebtItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TechnicalDebtItemRepository extends JpaRepository<TechnicalDebtItem, UUID> {
    Optional<TechnicalDebtItem> findByItemCode(String itemCode);
}
