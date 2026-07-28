package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMePriceBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyForMePriceBreakdownRepository extends JpaRepository<BuyForMePriceBreakdown, UUID> {
    Optional<BuyForMePriceBreakdown> findByOrderId(UUID orderId);
}
