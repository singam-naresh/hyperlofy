package com.hyperlofy.backend.notification.service;

import com.hyperlofy.backend.notification.entity.NotificationCampaign;
import com.hyperlofy.backend.notification.entity.NotificationConsent;
import com.hyperlofy.backend.notification.entity.NotificationEngagement;
import com.hyperlofy.backend.notification.entity.NotificationJourney;
import com.hyperlofy.backend.notification.repository.NotificationCampaignRepository;
import com.hyperlofy.backend.notification.repository.NotificationConsentRepository;
import com.hyperlofy.backend.notification.repository.NotificationEngagementRepository;
import com.hyperlofy.backend.notification.repository.NotificationJourneyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(NotificationEnterpriseService.class);

    private final NotificationCampaignRepository campaignRepository;
    private final NotificationJourneyRepository journeyRepository;
    private final NotificationConsentRepository consentRepository;
    private final NotificationEngagementRepository engagementRepository;

    @Transactional
    public NotificationJourney startCustomerJourney(UUID userId, String journeyName, String initialStep) {
        log.info("[NOTIFICATIONS ENTERPRISE] Starting automated customer journey UserId={}, Journey={}, Step={}",
                userId, journeyName, initialStep);

        NotificationJourney journey = NotificationJourney.builder()
                .userId(userId)
                .journeyName(journeyName)
                .currentStep(initialStep)
                .status("IN_PROGRESS")
                .startedAt(ZonedDateTime.now())
                .build();

        return journeyRepository.save(journey);
    }

    @Transactional
    public NotificationCampaign createCampaign(String campaignName, String channel, String targetSegment, String templateCode, String createdBy) {
        log.info("[NOTIFICATIONS ENTERPRISE] Creating targeted marketing campaign Name={}, Channel={}, Segment={}",
                campaignName, channel, targetSegment);

        NotificationCampaign campaign = NotificationCampaign.builder()
                .campaignName(campaignName)
                .channel(channel.toUpperCase())
                .targetSegment(targetSegment)
                .templateCode(templateCode)
                .status("APPROVED")
                .createdBy(createdBy)
                .scheduledAt(ZonedDateTime.now().plusHours(2))
                .build();

        return campaignRepository.save(campaign);
    }

    @Transactional
    public NotificationConsent updateMarketingConsent(UUID userId, Boolean marketing, Boolean transactional) {
        log.info("[NOTIFICATIONS ENTERPRISE] Updating regulatory consent for UserId={}, Marketing={}, Transactional={}",
                userId, marketing, transactional);

        NotificationConsent consent = consentRepository.findByUserId(userId).orElseGet(() ->
                NotificationConsent.builder().userId(userId).build()
        );

        if (marketing != null) consent.setMarketingConsent(marketing);
        if (transactional != null) consent.setTransactionalConsent(transactional);
        consent.setConsentGivenAt(ZonedDateTime.now());

        return consentRepository.save(consent);
    }

    @Transactional
    public NotificationEngagement trackEngagement(UUID messageId, String eventType) {
        log.info("[NOTIFICATIONS ENTERPRISE] Tracking cross-channel engagement MessageId={}, Event={}", messageId, eventType);

        NotificationEngagement engagement = NotificationEngagement.builder()
                .messageId(messageId)
                .eventType(eventType)
                .timestamp(ZonedDateTime.now())
                .build();

        return engagementRepository.save(engagement);
    }
}
