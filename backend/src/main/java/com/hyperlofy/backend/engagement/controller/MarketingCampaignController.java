package com.hyperlofy.backend.engagement.controller;

import com.hyperlofy.backend.engagement.entity.MarketingCampaign;
import com.hyperlofy.backend.engagement.service.AiCustomerEngagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Tag(name = "Marketing Campaign Automation API", description = "Automated festival, win-back, welcome, birthday, and flash sale campaigns with recipient tracking and conversion analytics")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MarketingCampaignController {

    private final AiCustomerEngagementService engagementService;

    @PostMapping
    @Operation(summary = "Create & Launch Automated Marketing Campaign", description = "Launches automated campaign (FESTIVAL_SALE, WINBACK, WELCOME, BIRTHDAY, FLASH_SALE) targeted to specific customer segments.")
    public ResponseEntity<MarketingCampaign> execute(
            @RequestParam String campaignCode,
            @RequestParam String campaignName,
            @RequestParam(required = false) String campaignType,
            @RequestParam(required = false) String targetSegment,
            @RequestParam(required = false) String couponCode) {
        return ResponseEntity.ok(engagementService.executeCampaign(campaignCode, campaignName, campaignType, targetSegment, couponCode));
    }

    @GetMapping
    @Operation(summary = "List Active Marketing Campaigns", description = "Returns active campaigns, total recipients, conversion rates, and discount coupon codes.")
    public ResponseEntity<List<MarketingCampaign>> getAll() {
        return ResponseEntity.ok(engagementService.getAllCampaigns());
    }
}
