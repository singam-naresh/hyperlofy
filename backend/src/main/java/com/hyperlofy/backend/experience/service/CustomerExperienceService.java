package com.hyperlofy.backend.experience.service;

import com.hyperlofy.backend.experience.entity.*;
import com.hyperlofy.backend.experience.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerExperienceService {

    private static final Logger log = LoggerFactory.getLogger(CustomerExperienceService.class);

    private final CustomerReviewRepository reviewRepository;
    private final ReviewRatingRepository ratingRepository;
    private final ReviewReplyRepository replyRepository;
    private final ReviewReactionRepository reactionRepository;
    private final ReviewReportRepository reportRepository;
    private final CustomerReputationRepository customerReputationRepository;
    private final MerchantReputationRepository merchantReputationRepository;

    @Transactional
    public CustomerReview createReview(String reviewCode, UUID customerId, UUID productId, UUID merchantId,
                                       UUID deliveryPartnerId, UUID orderId, String title, String content,
                                       BigDecimal rating, String mediaUrls, UUID tenantId) {
        log.info("[CUSTOMER EXPERIENCE] Creating review Code={}, Customer={}, Product={}, Rating={}", reviewCode, customerId, productId, rating);

        CustomerReview review = reviewRepository.findByReviewCode(reviewCode).orElseGet(() ->
                CustomerReview.builder()
                        .reviewCode(reviewCode)
                        .customerId(customerId)
                        .productId(productId)
                        .merchantId(merchantId)
                        .deliveryPartnerId(deliveryPartnerId)
                        .orderId(orderId)
                        .title(title)
                        .content(content)
                        .rating(rating != null ? rating : new BigDecimal("5.00"))
                        .isVerifiedPurchase(true)
                        .status("APPROVED")
                        .aiTrustScore(new BigDecimal("98.50"))
                        .helpfulCount(0)
                        .mediaUrls(mediaUrls)
                        .tenantId(tenantId)
                        .build()
        );

        review = reviewRepository.save(review);

        ReviewRating ratingBreakdown = ReviewRating.builder()
                .review(review)
                .qualityRating(rating)
                .packagingRating(rating)
                .deliveryRating(rating)
                .valueRating(rating)
                .communicationRating(rating)
                .build();
        ratingRepository.save(ratingBreakdown);

        return review;
    }

    @Transactional
    public ReviewReply replyToReview(UUID reviewId, UUID replierUserId, String replierRole, String content) {
        log.info("[CUSTOMER EXPERIENCE] Merchant/Support reply to ReviewId={}, Replier={}", reviewId, replierUserId);

        CustomerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        ReviewReply reply = ReviewReply.builder()
                .review(review)
                .replierUserId(replierUserId)
                .replierRole(replierRole != null ? replierRole : "MERCHANT")
                .content(content)
                .isPinned(false)
                .build();

        return replyRepository.save(reply);
    }

    @Transactional
    public ReviewReaction addReaction(UUID reviewId, UUID userId, String reactionType) {
        log.info("[CUSTOMER EXPERIENCE] Reaction to ReviewId={}, User={}, Type={}", reviewId, userId, reactionType);

        CustomerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        ReviewReaction reaction = ReviewReaction.builder()
                .review(review)
                .userId(userId)
                .reactionType(reactionType != null ? reactionType : "HELPFUL")
                .build();

        if ("HELPFUL".equalsIgnoreCase(reactionType)) {
            review.setHelpfulCount(review.getHelpfulCount() + 1);
            reviewRepository.save(review);
        }

        return reactionRepository.save(reaction);
    }

    @Transactional
    public ReviewReport reportReview(UUID reviewId, UUID reporterUserId, String reason, String comments) {
        log.info("[CUSTOMER EXPERIENCE] Reporting ReviewId={}, Reporter={}, Reason={}", reviewId, reporterUserId, reason);

        CustomerReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        ReviewReport report = ReviewReport.builder()
                .review(review)
                .reporterUserId(reporterUserId)
                .reason(reason)
                .comments(comments)
                .status("PENDING")
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public CustomerReputation getCustomerReputation(UUID customerId) {
        return customerReputationRepository.findByCustomerId(customerId).orElseGet(() ->
                CustomerReputation.builder()
                        .customerId(customerId)
                        .reputationScore(new BigDecimal("95.00"))
                        .verifiedPurchaseRatio(new BigDecimal("100.00"))
                        .helpfulVotesReceived(12)
                        .badgeLevel("GOLD_REVIEWER")
                        .communityTrustLevel("HIGHLY_TRUSTED")
                        .totalReviewsCount(15)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public MerchantReputation getMerchantReputation(UUID merchantId) {
        return merchantReputationRepository.findByMerchantId(merchantId).orElseGet(() ->
                MerchantReputation.builder()
                        .merchantId(merchantId)
                        .averageRating(new BigDecimal("4.85"))
                        .totalReviewsCount(142)
                        .csatScorePercent(new BigDecimal("96.50"))
                        .avgResponseTimeHours(new BigDecimal("2.40"))
                        .complaintRatioPercent(new BigDecimal("0.80"))
                        .aiTrustScore(new BigDecimal("99.00"))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<CustomerReview> getReviewsByProduct(UUID productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<CustomerReview> getReviewsByMerchant(UUID merchantId) {
        return reviewRepository.findByMerchantId(merchantId);
    }

    @Transactional(readOnly = true)
    public List<CustomerReview> getReviewsByCustomer(UUID customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }
}
