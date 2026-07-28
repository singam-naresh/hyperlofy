package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {
    Optional<PricingRule> findByServiceTypeAndIsActiveTrue(String serviceType);
}
