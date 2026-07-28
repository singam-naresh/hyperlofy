package com.hyperlofy.backend.notification.service;

import com.hyperlofy.backend.notification.entity.NotificationMessage;
import com.hyperlofy.backend.notification.entity.NotificationPreference;
import com.hyperlofy.backend.notification.entity.NotificationProvider;
import com.hyperlofy.backend.notification.repository.NotificationMessageRepository;
import com.hyperlofy.backend.notification.repository.NotificationPreferenceRepository;
import com.hyperlofy.backend.notification.repository.NotificationProviderRepository;
import com.hyperlofy.backend.notification.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationEngineService {

    private static final Logger log = LoggerFactory.getLogger(NotificationEngineService.class);

    private final NotificationMessageRepository messageRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationProviderRepository providerRepository;

    @Transactional
    public NotificationMessage sendNotification(UUID recipientId, String channel, String title, String body, String templateCode) {
        log.info("[NOTIFICATIONS ENGINE] Dispatching multi-channel message RecipientId={}, Channel={}, Template={}",
                recipientId, channel, templateCode);

        // Check recipient user preferences
        NotificationPreference preference = preferenceRepository.findByUserId(recipientId).orElseGet(() ->
                NotificationPreference.builder()
                        .userId(recipientId)
                        .pushEnabled(true)
                        .smsEnabled(true)
                        .emailEnabled(true)
                        .whatsappEnabled(true)
                        .quietHoursEnabled(false)
                        .build()
        );

        if ("SMS".equalsIgnoreCase(channel) && Boolean.FALSE.equals(preference.getSmsEnabled())) {
            log.warn("[NOTIFICATIONS ENGINE] SMS opt-out preference active for user: {}", recipientId);
        }

        // Select optimal active provider
        List<NotificationProvider> providers = providerRepository.findByChannelAndIsActiveTrueOrderByPriorityAsc(channel.toUpperCase());
        String providerName = providers.isEmpty() ? "DEFAULT_" + channel.toUpperCase() + "_GATEWAY" : providers.get(0).getProviderName();

        NotificationMessage message = NotificationMessage.builder()
                .recipientId(recipientId)
                .channel(channel.toUpperCase())
                .templateCode(templateCode)
                .title(title)
                .body(body)
                .status("SENT")
                .providerName(providerName)
                .deliveryAttempts(1)
                .deliveredAt(ZonedDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    @Transactional
    public NotificationPreference updatePreferences(UUID userId, Boolean push, Boolean sms, Boolean email, Boolean whatsapp) {
        log.info("[NOTIFICATIONS ENGINE] Updating user channel preferences for UserId={}", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId).orElseGet(() ->
                NotificationPreference.builder().userId(userId).build()
        );

        if (push != null) preference.setPushEnabled(push);
        if (sms != null) preference.setSmsEnabled(sms);
        if (email != null) preference.setEmailEnabled(email);
        if (whatsapp != null) preference.setWhatsappEnabled(whatsapp);

        return preferenceRepository.save(preference);
    }

    @Transactional(readOnly = true)
    public List<NotificationMessage> getRecipientNotifications(UUID recipientId) {
        return messageRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Transactional
    public NotificationMessage markAsRead(UUID messageId) {
        NotificationMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Notification message not found: " + messageId));

        message.setStatus("READ");
        message.setReadAt(ZonedDateTime.now());
        return messageRepository.save(message);
    }
}
