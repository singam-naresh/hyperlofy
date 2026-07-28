package com.hyperlofy.backend.notification.controller;

import com.hyperlofy.backend.notification.entity.NotificationCampaign;
import com.hyperlofy.backend.notification.entity.NotificationConsent;
import com.hyperlofy.backend.notification.entity.NotificationEngagement;
import com.hyperlofy.backend.notification.entity.NotificationJourney;
import com.hyperlofy.backend.notification.service.NotificationEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/enterprise")
@RequiredArgsConstructor
@Tag(name = "Notifications Engine Enterprise Addendum API", description = "Endpoints for Omnichannel Journey Automation, AI Message Optimization, Targeted Campaigns, and GDPR/TCPA Consent Governance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class NotificationEnterpriseController {

    private final NotificationEnterpriseService enterpriseService;

    @PostMapping("/journey/start")
    @Operation(summary = "Start Customer Journey Automation", description = "Triggers automated omnichannel messaging journey (e.g. Order Fulfillment Journey, Re-engagement Journey).")
    public ResponseEntity<NotificationJourney> startJourney(
            @RequestParam UUID userId,
            @RequestParam String journeyName,
            @RequestParam String initialStep) {
        return ResponseEntity.ok(enterpriseService.startCustomerJourney(userId, journeyName, initialStep));
    }

    @PostMapping("/campaign/create")
    @Operation(summary = "Create Marketing Campaign", description = "Schedules audience-segmented broadcast campaign across Push, SMS, Email, or WhatsApp.")
    public ResponseEntity<NotificationCampaign> createCampaign(
            @RequestParam String campaignName,
            @RequestParam String channel,
            @RequestParam String targetSegment,
            @RequestParam String templateCode,
            @RequestParam String createdBy) {
        return ResponseEntity.ok(enterpriseService.createCampaign(campaignName, channel, targetSegment, templateCode, createdBy));
    }

    @PutMapping("/consent")
    @Operation(summary = "Update Regulatory Marketing Consent", description = "Records user consent state for promotional and transactional messaging compliance.")
    public ResponseEntity<NotificationConsent> updateConsent(
            @RequestParam UUID userId,
            @RequestParam(required = false) Boolean marketing,
            @RequestParam(required = false) Boolean transactional) {
        return ResponseEntity.ok(enterpriseService.updateMarketingConsent(userId, marketing, transactional));
    }

    @PostMapping("/engagement/track")
    @Operation(summary = "Track Message Engagement Event", description = "Records click-through rate, open event, or conversion event for AI message optimization.")
    public ResponseEntity<NotificationEngagement> trackEngagement(
            @RequestParam UUID messageId,
            @RequestParam String eventType) {
        return ResponseEntity.ok(enterpriseService.trackEngagement(messageId, eventType));
    }
}
