package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.SystemNotification;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Platform Notification Center API", description = "Endpoints for broadcasting push/email/SMS notifications to customers, merchants, and delivery partners")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class NotificationCenterController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "List System Notifications", description = "Retrieves broadcast notification history.")
    public ResponseEntity<List<SystemNotification>> getAllNotifications() {
        return ResponseEntity.ok(platformService.getAllNotifications());
    }

    @PostMapping("/broadcast")
    @Operation(summary = "Broadcast System Notification", description = "Sends a system notification broadcast across push/email/SMS.")
    public ResponseEntity<SystemNotification> broadcastNotification(@Valid @RequestBody SystemNotification notification) {
        return ResponseEntity.ok(platformService.broadcastNotification(notification));
    }
}
