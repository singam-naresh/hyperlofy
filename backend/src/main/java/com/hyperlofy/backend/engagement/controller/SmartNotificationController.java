package com.hyperlofy.backend.engagement.controller;

import com.hyperlofy.backend.engagement.entity.NotificationDecision;
import com.hyperlofy.backend.engagement.service.AiCustomerEngagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Smart Notification Decision Engine API", description = "AI notification timing, optimal delivery channel (Push, Email, SMS, WhatsApp), priority, and decision explanations")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SmartNotificationController {

    private final AiCustomerEngagementService engagementService;

    @PostMapping("/decision")
    @Operation(summary = "Determine Smart Notification Decision", description = "Determines optimal channel (Push, Email, SMS, WhatsApp), best delivery time, and priority based on customer historical activity.")
    public ResponseEntity<NotificationDecision> decide(
            @RequestParam String decisionCode,
            @RequestParam UUID customerId,
            @RequestParam String triggerEvent,
            @RequestParam(required = false) String optimalChannel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime deliveryTime,
            @RequestParam(required = false) String explanation) {
        return ResponseEntity.ok(engagementService.decideNotification(decisionCode, customerId, triggerEvent, optimalChannel, deliveryTime, explanation));
    }
}
