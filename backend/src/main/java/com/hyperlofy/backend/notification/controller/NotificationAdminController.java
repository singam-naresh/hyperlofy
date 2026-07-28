package com.hyperlofy.backend.notification.controller;

import com.hyperlofy.backend.notification.entity.NotificationMessage;
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
@RequestMapping("/api/v1/notifications/admin")
@RequiredArgsConstructor
@Tag(name = "Notifications Engine Admin API", description = "Endpoints for administrators to oversee platform communications, delivery success rates, and gateway providers")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class NotificationAdminController {

    private final NotificationEngineService notificationService;

    @GetMapping("/recipient/{recipientId}")
    @Operation(summary = "Admin Inspect Recipient Notifications", description = "Returns full delivery history and provider metadata for a recipient.")
    public ResponseEntity<List<NotificationMessage>> inspectNotifications(@PathVariable UUID recipientId) {
        return ResponseEntity.ok(notificationService.getRecipientNotifications(recipientId));
    }
}
