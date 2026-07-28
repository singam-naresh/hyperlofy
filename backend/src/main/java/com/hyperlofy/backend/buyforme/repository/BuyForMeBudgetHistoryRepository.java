package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMeBudgetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuyForMeBudgetHistoryRepository extends JpaRepository<BuyForMeBudgetHistory, UUID> {
    List<BuyForMeBudgetHistory> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
