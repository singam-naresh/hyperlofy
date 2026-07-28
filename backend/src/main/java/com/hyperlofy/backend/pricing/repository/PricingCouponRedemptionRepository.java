package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingCouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingCouponRedemptionRepository extends JpaRepository<PricingCouponRedemption, UUID> {
    Optional<PricingCouponRedemption> findByCouponIdAndUserId(UUID couponId, UUID userId);
}
