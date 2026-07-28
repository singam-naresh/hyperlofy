package com.hyperlofy.backend.experience.controller;

import com.hyperlofy.backend.experience.entity.CustomerReview;
import com.hyperlofy.backend.experience.entity.ReviewReaction;
import com.hyperlofy.backend.experience.entity.ReviewReply;
import com.hyperlofy.backend.experience.entity.ReviewReport;
import com.hyperlofy.backend.experience.service.CustomerExperienceService;
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
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Customer Experience & Reviews API", description = "Submit verified customer reviews, merchant replies, helpful reactions, and abuse reporting across products and merchants")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CustomerReviewController {

    private final CustomerExperienceService experienceService;

    @PostMapping
    @Operation(summary = "Submit Customer Review", description = "Submits a verified purchase customer review with 1–5 star rating, content, media attachments, and AI trust scoring.")
    public ResponseEntity<CustomerReview> createReview(
            @RequestParam String reviewCode,
            @RequestParam UUID customerId,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) UUID deliveryPartnerId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) BigDecimal rating,
            @RequestParam(required = false) String mediaUrls,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(experienceService.createReview(reviewCode, customerId, productId, merchantId, deliveryPartnerId, orderId, title, content, rating, mediaUrls, tenantId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get Customer Reviews by Product ID", description = "Returns list of approved customer reviews, rating breakdowns, and media attachments for specified product.")
    public ResponseEntity<List<CustomerReview>> getProductReviews(@PathVariable UUID productId) {
        return ResponseEntity.ok(experienceService.getReviewsByProduct(productId));
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get Customer Reviews by Merchant ID", description = "Returns list of approved merchant reviews and store ratings.")
    public ResponseEntity<List<CustomerReview>> getMerchantReviews(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(experienceService.getReviewsByMerchant(merchantId));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get Customer Review History", description = "Returns historical reviews authored by customer ID.")
    public ResponseEntity<List<CustomerReview>> getCustomerReviews(@PathVariable UUID customerId) {
        return ResponseEntity.ok(experienceService.getReviewsByCustomer(customerId));
    }

    @PostMapping("/{id}/reply")
    @Operation(summary = "Merchant Reply to Review", description = "Allows merchant or support representative to post official reply to customer review.")
    public ResponseEntity<ReviewReply> reply(
            @PathVariable UUID id,
            @RequestParam UUID replierUserId,
            @RequestParam(required = false) String replierRole,
            @RequestParam String content) {
        return ResponseEntity.ok(experienceService.replyToReview(id, replierUserId, replierRole, content));
    }

    @PostMapping("/{id}/reaction")
    @Operation(summary = "Add Review Reaction / Helpful Vote", description = "Posts reaction (HELPFUL, LIKE, LOVE, FUNNY, DISAGREE) to customer review.")
    public ResponseEntity<ReviewReaction> reaction(
            @PathVariable UUID id,
            @RequestParam UUID userId,
            @RequestParam(required = false) String reactionType) {
        return ResponseEntity.ok(experienceService.addReaction(id, userId, reactionType));
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "Report Review Abuse / Spam", description = "Flags customer review for moderation review (SPAM, FAKE_REVIEW, OFFENSIVE_LANGUAGE, HATE_SPEECH).")
    public ResponseEntity<ReviewReport> report(
            @PathVariable UUID id,
            @RequestParam UUID reporterUserId,
            @RequestParam String reason,
            @RequestParam(required = false) String comments) {
        return ResponseEntity.ok(experienceService.reportReview(id, reporterUserId, reason, comments));
    }
}
