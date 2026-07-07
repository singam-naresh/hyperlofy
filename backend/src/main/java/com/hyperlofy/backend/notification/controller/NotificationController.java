package com.hyperlofy.backend.notification.controller;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.notification.entity.Notification;
import com.hyperlofy.backend.notification.service.NotificationService;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        User user = getCurrentAuthenticatedUser();
        return ResponseEntity.ok(notificationService.getMyNotifications(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getMyUnreadNotifications() {
        User user = getCurrentAuthenticatedUser();
        return ResponseEntity.ok(notificationService.getMyUnreadNotifications(user.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> readAll() {
        User user = getCurrentAuthenticatedUser();
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.UNAUTHORIZED));
    }
}
