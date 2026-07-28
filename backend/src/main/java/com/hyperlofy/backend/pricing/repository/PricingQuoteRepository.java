package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingQuoteRepository extends JpaRepository<PricingQuote, UUID> {
    Optional<PricingQuote> findByOrderId(UUID orderId);
}
