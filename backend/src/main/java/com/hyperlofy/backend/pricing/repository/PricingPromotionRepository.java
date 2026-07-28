package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PricingPromotionRepository extends JpaRepository<PricingPromotion, UUID> {
}
