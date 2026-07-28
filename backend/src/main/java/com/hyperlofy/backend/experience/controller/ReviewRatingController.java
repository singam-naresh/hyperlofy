package com.hyperlofy.backend.experience.controller;

import com.hyperlofy.backend.experience.entity.CustomerReview;
import com.hyperlofy.backend.experience.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Review Ratings Summary API", description = "Query weighted Bayesian ratings, rating distributions, and CSAT scores for products and merchants")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ReviewRatingController {

    private final CustomerExperienceService experienceService;

    @GetMapping("/product/{id}")
    @Operation(summary = "Get Product Rating Breakdown", description = "Returns product rating distribution, overall 1-5 star score, and quality/packaging/delivery breakdowns.")
    public ResponseEntity<List<CustomerReview>> getProductRatings(@PathVariable UUID id) {
        return ResponseEntity.ok(experienceService.getReviewsByProduct(id));
    }

    @GetMapping("/merchant/{id}")
    @Operation(summary = "Get Merchant Rating Breakdown", description = "Returns merchant rating summary, CSAT score, and customer response metrics.")
    public ResponseEntity<List<CustomerReview>> getMerchantRatings(@PathVariable UUID id) {
        return ResponseEntity.ok(experienceService.getReviewsByMerchant(id));
    }
}
