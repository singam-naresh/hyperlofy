package com.hyperlofy.backend.pricing.controller;

import com.hyperlofy.backend.pricing.entity.PricingCouponRedemption;
import com.hyperlofy.backend.pricing.entity.PricingPromotion;
import com.hyperlofy.backend.pricing.service.DynamicPricingEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pricing/enterprise")
@RequiredArgsConstructor
@Tag(name = "Dynamic Pricing Engine Enterprise Addendum API", description = "Endpoints for coupon redemption, promotion campaign creation, four-eye revenue governance, and AI price recommendations")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DynamicPricingEnterpriseController {

    private final DynamicPricingEnterpriseService enterpriseService;

    @PostMapping("/coupons/apply")
    @Operation(summary = "Apply Coupon Code to Order", description = "Validates coupon single-use rules, max redemptions limit, and calculates discount amount.")
    public ResponseEntity<PricingCouponRedemption> applyCoupon(
            @RequestParam String couponCode,
            @RequestParam UUID userId,
            @RequestParam UUID orderId,
            @RequestParam BigDecimal orderSubtotal) {
        return ResponseEntity.ok(enterpriseService.applyCouponCode(couponCode, userId, orderId, orderSubtotal));
    }

    @PostMapping("/promotions")
    @Operation(summary = "Create Promotion Campaign", description = "Configures flat or percentage promotional discounts for marketing campaigns.")
    public ResponseEntity<PricingPromotion> createPromotion(
            @RequestParam String title,
            @RequestParam String discountType,
            @RequestParam BigDecimal discountValue,
            @RequestParam(required = false) BigDecimal maxDiscount,
            @RequestParam(required = false) BigDecimal minOrder) {
        return ResponseEntity.ok(enterpriseService.createPromotionCampaign(title, discountType, discountValue, maxDiscount, minOrder));
    }
}
