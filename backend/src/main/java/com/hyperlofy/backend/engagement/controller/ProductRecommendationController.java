package com.hyperlofy.backend.engagement.controller;

import com.hyperlofy.backend.engagement.entity.ProductRecommendation;
import com.hyperlofy.backend.engagement.service.AiCustomerEngagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Tag(name = "AI Recommendation Engine API", description = "Personalized product, merchant, and category recommendations using collaborative filtering and purchase similarity")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ProductRecommendationController {

    private final AiCustomerEngagementService engagementService;

    @GetMapping("/products")
    @Operation(summary = "Get Personalised Product Recommendations", description = "Returns personalized product recommendations based on collaborative filtering and purchase similarity.")
    public ResponseEntity<List<ProductRecommendation>> getProducts(@RequestParam UUID customerId) {
        return ResponseEntity.ok(engagementService.getRecommendationsByCustomer(customerId));
    }

    @PostMapping
    @Operation(summary = "Generate AI Recommendation", description = "Generates product or merchant recommendation with similarity score and model attribution.")
    public ResponseEntity<ProductRecommendation> generate(
            @RequestParam String recommendationCode,
            @RequestParam UUID customerId,
            @RequestParam UUID productId,
            @RequestParam(required = false) String recType,
            @RequestParam(required = false) BigDecimal similarity) {
        return ResponseEntity.ok(engagementService.generateRecommendation(recommendationCode, customerId, productId, recType, similarity));
    }
}
