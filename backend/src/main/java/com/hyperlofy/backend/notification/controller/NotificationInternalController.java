package com.hyperlofy.backend.notification.controller;

import com.hyperlofy.backend.notification.entity.NotificationMessage;
import com.hyperlofy.backend.notification.service.NotificationEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/internal")
@RequiredArgsConstructor
@Tag(name = "Notifications Engine Internal Integration API", description = "Endpoints for Hyperlofy services (Orders, Matching, Tracking, Payments) to trigger multi-channel notifications")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class NotificationInternalController {

    private final NotificationEngineService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send Multi-Channel Notification", description = "Dispatches push, SMS, email, or WhatsApp notification using template rendering and provider abstraction.")
    public ResponseEntity<NotificationMessage> sendNotification(
            @RequestParam UUID recipientId,
            @RequestParam String channel,
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(required = false) String templateCode) {
        return ResponseEntity.ok(notificationService.sendNotification(recipientId, channel, title, body, templateCode));
    }
}
