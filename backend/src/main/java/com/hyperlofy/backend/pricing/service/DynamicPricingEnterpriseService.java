package com.hyperlofy.backend.pricing.service;

import com.hyperlofy.backend.pricing.entity.PricingCoupon;
import com.hyperlofy.backend.pricing.entity.PricingCouponRedemption;
import com.hyperlofy.backend.pricing.entity.PricingPromotion;
import com.hyperlofy.backend.pricing.repository.PricingCouponRedemptionRepository;
import com.hyperlofy.backend.pricing.repository.PricingCouponRepository;
import com.hyperlofy.backend.pricing.repository.PricingPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicPricingEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(DynamicPricingEnterpriseService.class);

    private final PricingPromotionRepository promotionRepository;
    private final PricingCouponRepository couponRepository;
    private final PricingCouponRedemptionRepository redemptionRepository;

    @Transactional
    public PricingCouponRedemption applyCouponCode(String couponCode, UUID userId, UUID orderId, BigDecimal orderSubtotal) {
        log.info("[DYNAMIC PRICING ENTERPRISE] Applying coupon code={} for UserId={}, OrderId={}, Subtotal={}", couponCode, userId, orderId, orderSubtotal);

        PricingCoupon coupon = couponRepository.findByCouponCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired coupon code: " + couponCode));

        if (coupon.getCurrentRedemptions() >= coupon.getMaxRedemptions()) {
            log.warn("[DYNAMIC PRICING ENTERPRISE] Coupon code {} has reached maximum redemptions limit", couponCode);
            throw new IllegalStateException("Coupon redemption limit reached");
        }

        PricingPromotion promotion = promotionRepository.findById(coupon.getPromotionId())
                .orElseThrow(() -> new IllegalArgumentException("Associated promotion not found for coupon: " + couponCode));

        BigDecimal discountAmount;
        if ("PERCENTAGE".equalsIgnoreCase(promotion.getDiscountType())) {
            discountAmount = orderSubtotal.multiply(promotion.getDiscountValue().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            if (promotion.getMaxDiscountAmount() != null) {
                discountAmount = discountAmount.min(promotion.getMaxDiscountAmount());
            }
        } else {
            discountAmount = promotion.getDiscountValue();
        }

        discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        coupon.setCurrentRedemptions(coupon.getCurrentRedemptions() + 1);
        couponRepository.save(coupon);

        PricingCouponRedemption redemption = PricingCouponRedemption.builder()
                .couponId(coupon.getId())
                .userId(userId)
                .orderId(orderId)
                .discountAmount(discountAmount)
                .redeemedAt(ZonedDateTime.now())
                .build();

        return redemptionRepository.save(redemption);
    }

    @Transactional
    public PricingPromotion createPromotionCampaign(String title, String discountType, BigDecimal discountValue, BigDecimal maxDiscount, BigDecimal minOrder) {
        log.info("[DYNAMIC PRICING ENTERPRISE] Creating Promotion Campaign title={}, Type={}, Value={}", title, discountType, discountValue);

        PricingPromotion promo = PricingPromotion.builder()
                .title(title)
                .discountType(discountType)
                .discountValue(discountValue)
                .maxDiscountAmount(maxDiscount)
                .minOrderAmount(minOrder != null ? minOrder : BigDecimal.ZERO)
                .isActive(true)
                .expiresAt(ZonedDateTime.now().plusDays(30))
                .build();

        return promotionRepository.save(promo);
    }
}
