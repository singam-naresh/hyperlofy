package com.hyperlofy.backend.pricing.repository;

import com.hyperlofy.backend.pricing.entity.PricingCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingCouponRepository extends JpaRepository<PricingCoupon, UUID> {
    Optional<PricingCoupon> findByCouponCodeAndIsActiveTrue(String couponCode);
}
