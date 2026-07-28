package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMeExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuyForMeExpenseRepository extends JpaRepository<BuyForMeExpense, UUID> {
    List<BuyForMeExpense> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
