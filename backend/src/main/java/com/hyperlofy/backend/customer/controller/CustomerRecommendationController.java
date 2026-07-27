package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.ai.recommendation.service.RecommendationService;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.platform.entity.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/recommendations")
@RequiredArgsConstructor
@Tag(name = "Customer AI Recommendation & Personalization API", description = "Endpoints for personalized merchant, product, coupon recommendations and trending topics")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerRecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/merchants")
    @Operation(summary = "Get Recommended Merchants", description = "Retrieves personalized store recommendations based on customer history and affinity.")
    public ResponseEntity<List<MerchantProfile>> getRecommendedMerchants(Principal principal) {
        return ResponseEntity.ok(recommendationService.getRecommendedMerchants(UUID.randomUUID()));
    }

    @GetMapping("/coupons")
    @Operation(summary = "Get Personalized Coupons", description = "Retrieves customized discount coupons tailored to customer preferences.")
    public ResponseEntity<List<Coupon>> getPersonalizedCoupons(Principal principal) {
        return ResponseEntity.ok(recommendationService.getPersonalizedCoupons(UUID.randomUUID()));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get Trending Items & Searches", description = "Retrieves trending stores, popular products, and top search terms.")
    public ResponseEntity<Map<String, Object>> getTrendingItems() {
        return ResponseEntity.ok(recommendationService.getTrendingItems());
    }

    @PostMapping("/track")
    @Operation(summary = "Track Customer Behaviour Event", description = "Records a user interaction event (product view, store view, search query) for personalization tuning.")
    public ResponseEntity<Void> trackEvent(
            Principal principal,
            @RequestParam String eventType,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String searchQuery) {

        recommendationService.trackEvent(UUID.randomUUID(), eventType, productId, merchantId, categoryId, searchQuery);
        return ResponseEntity.ok().build();
    }
}
