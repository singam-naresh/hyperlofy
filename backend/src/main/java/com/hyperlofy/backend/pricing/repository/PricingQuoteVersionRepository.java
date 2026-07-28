package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingQuoteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PricingQuoteVersionRepository extends JpaRepository<PricingQuoteVersion, UUID> {
    List<PricingQuoteVersion> findByQuoteIdOrderByVersionNumberAsc(UUID quoteId);
}
