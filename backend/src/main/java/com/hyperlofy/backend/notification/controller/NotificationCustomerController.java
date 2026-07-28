package com.hyperlofy.backend.notification.controller;

import com.hyperlofy.backend.notification.entity.NotificationMessage;
import com.hyperlofy.backend.notification.entity.NotificationPreference;
import com.hyperlofy.backend.notification.service.NotificationEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/customer")
@RequiredArgsConstructor
@Tag(name = "Notifications Engine Customer API", description = "Endpoints for users to view in-app notifications and manage channel delivery preferences")
@PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'MERCHANT')")
public class NotificationCustomerController {

    private final NotificationEngineService notificationService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get User In-App Notifications", description = "Returns chronological list of notifications delivered to the user.")
    public ResponseEntity<List<NotificationMessage>> getNotifications(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getRecipientNotifications(userId));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update Notification Channel Preferences", description = "Allows user to opt in or out of Push, SMS, Email, and WhatsApp messaging.")
    public ResponseEntity<NotificationPreference> updatePreferences(
            @RequestParam UUID userId,
            @RequestParam(required = false) Boolean push,
            @RequestParam(required = false) Boolean sms,
            @RequestParam(required = false) Boolean email,
            @RequestParam(required = false) Boolean whatsapp) {
        return ResponseEntity.ok(notificationService.updatePreferences(userId, push, sms, email, whatsapp));
    }

    @PutMapping("/read/{messageId}")
    @Operation(summary = "Mark Notification as Read", description = "Updates message status to READ with delivery timestamp.")
    public ResponseEntity<NotificationMessage> markAsRead(@PathVariable UUID messageId) {
        return ResponseEntity.ok(notificationService.markAsRead(messageId));
    }
}
