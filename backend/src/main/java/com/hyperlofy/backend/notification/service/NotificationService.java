package com.hyperlofy.backend.notification.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.notification.entity.Notification;
import com.hyperlofy.backend.notification.repository.NotificationRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Notification sendNotification(UUID userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .read(false)
                .type(type)
                .build();

        log.info("Sending notification titled [{}] for user role profile: {}", title, user.getEmail());
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getMyNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Notification> getMyUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
    }

    @Transactional
    public Notification markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("Notification not found", HttpStatus.NOT_FOUND));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        log.info("Finished marking all notifications read for client ID: {}", userId);
    }
}
