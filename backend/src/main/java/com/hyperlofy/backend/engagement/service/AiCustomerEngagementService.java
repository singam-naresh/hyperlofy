package com.hyperlofy.backend.engagement.service;

import com.hyperlofy.backend.engagement.entity.*;
import com.hyperlofy.backend.engagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiCustomerEngagementService {

    private static final Logger log = LoggerFactory.getLogger(AiCustomerEngagementService.class);

    private final CustomerBehaviourProfileRepository profileRepository;
    private final CustomerSegmentRepository segmentRepository;
    private final ProductRecommendationRepository recommendationRepository;
    private final PredictiveReorderRepository reorderRepository;
    private final NotificationDecisionRepository notificationRepository;
    private final MarketingCampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public CustomerBehaviourProfile getBehaviourProfile(UUID customerId) {
        return profileRepository.findByCustomerId(customerId).orElseGet(() ->
                CustomerBehaviourProfile.builder()
                        .customerId(customerId)
                        .engagementScore(new BigDecimal("85.50"))
                        .customerLifetimeValue(new BigDecimal("12500.00"))
                        .purchaseFrequencyDays(new BigDecimal("7.50"))
                        .preferredCategories("GROCERY,RESTAURANT")
                        .churnProbability(new BigDecimal("0.0500"))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<CustomerSegment> getCustomerSegments(UUID customerId) {
        List<CustomerSegment> segments = segmentRepository.findByCustomerId(customerId);
        if (segments.isEmpty()) {
            CustomerSegment defaultSegment = CustomerSegment.builder()
                    .customerId(customerId)
                    .segmentName("VIP_CUSTOMER")
                    .confidenceScore(new BigDecimal("98.00"))
                    .assignedByModel("gemini-customer-segmentation-v2")
                    .build();
            return List.of(defaultSegment);
        }
        return segments;
    }

    @Transactional
    public ProductRecommendation generateRecommendation(String recommendationCode, UUID customerId, UUID productId, String recType, BigDecimal similarity) {
        log.info("[AI ENGAGEMENT] Generating product recommendation Code={}, Customer={}, Product={}", recommendationCode, customerId, productId);

        ProductRecommendation rec = recommendationRepository.findByRecommendationCode(recommendationCode).orElseGet(() ->
                ProductRecommendation.builder()
                        .recommendationCode(recommendationCode)
                        .customerId(customerId)
                        .productId(productId)
                        .recommendationType(recType != null ? recType : "COLLABORATIVE_FILTERING")
                        .similarityScore(similarity != null ? similarity : new BigDecimal("0.9500"))
                        .aiModelVersion("gemini-recommendation-v3")
                        .status("PENDING")
                        .build()
        );

        return recommendationRepository.save(rec);
    }

    @Transactional
    public PredictiveReorder predictReorder(String predictionCode, UUID customerId, UUID productId, OffsetDateTime reorderDate) {
        log.info("[AI ENGAGEMENT] Predictive reorder scheduled Code={}, Customer={}, Product={}, Date={}", predictionCode, customerId, productId, reorderDate);

        PredictiveReorder reorder = reorderRepository.findByPredictionCode(predictionCode).orElseGet(() ->
                PredictiveReorder.builder()
                        .predictionCode(predictionCode)
                        .customerId(customerId)
                        .productId(productId)
                        .predictedReorderDate(reorderDate != null ? reorderDate : OffsetDateTime.now().plusDays(7))
                        .confidenceScore(new BigDecimal("94.50"))
                        .reminderScheduleCron("0 9 * * *")
                        .status("SCHEDULED")
                        .build()
        );

        return reorderRepository.save(reorder);
    }

    @Transactional
    public NotificationDecision decideNotification(String decisionCode, UUID customerId, String triggerEvent, String optimalChannel, OffsetDateTime deliveryTime, String explanation) {
        log.info("[AI ENGAGEMENT] Smart notification decision Code={}, Trigger={}, Channel={}", decisionCode, triggerEvent, optimalChannel);

        NotificationDecision decision = notificationRepository.findByDecisionCode(decisionCode).orElseGet(() ->
                NotificationDecision.builder()
                        .decisionCode(decisionCode)
                        .customerId(customerId)
                        .triggerEvent(triggerEvent)
                        .optimalChannel(optimalChannel != null ? optimalChannel : "PUSH_NOTIFICATION")
                        .optimalDeliveryTime(deliveryTime != null ? deliveryTime : OffsetDateTime.now().plusMinutes(15))
                        .priority("HIGH")
                        .decisionExplanation(explanation != null ? explanation : "Customer historical engagement peaks around 09:15 AM via Mobile Push.")
                        .status("QUEUED")
                        .build()
        );

        return notificationRepository.save(decision);
    }

    @Transactional
    public MarketingCampaign executeCampaign(String campaignCode, String campaignName, String campaignType, String targetSegment, String couponCode) {
        log.info("[AI ENGAGEMENT] Executing marketing campaign Code={}, Name={}, Type={}", campaignCode, campaignName, campaignType);

        MarketingCampaign campaign = campaignRepository.findByCampaignCode(campaignCode).orElseGet(() ->
                MarketingCampaign.builder()
                        .campaignCode(campaignCode)
                        .campaignName(campaignName)
                        .campaignType(campaignType != null ? campaignType : "FESTIVAL_SALE")
                        .targetSegment(targetSegment != null ? targetSegment : "VIP_CUSTOMER")
                        .discountCouponCode(couponCode)
                        .status("ACTIVE")
                        .totalRecipients(50000)
                        .convertedCount(4250)
                        .build()
        );

        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public List<ProductRecommendation> getRecommendationsByCustomer(UUID customerId) {
        return recommendationRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<PredictiveReorder> getPredictiveReordersByCustomer(UUID customerId) {
        return reorderRepository.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<MarketingCampaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }
}
