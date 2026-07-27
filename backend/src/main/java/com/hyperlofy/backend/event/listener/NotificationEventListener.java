package com.hyperlofy.backend.event.listener;

import com.hyperlofy.backend.event.domain.NotificationRequestedEvent;
import com.hyperlofy.backend.event.service.RealtimeMessagingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);
    private final RealtimeMessagingService realtimeMessagingService;

    @Async
    @EventListener
    public void handleNotificationRequested(NotificationRequestedEvent event) {
        log.info("Async handling NotificationRequestedEvent for recipientId={}, type={}", event.getRecipientId(), event.getNotificationType());

        Map<String, String> payload = new HashMap<>();
        payload.put("title", event.getTitle());
        payload.put("message", event.getMessage());
        payload.put("type", event.getNotificationType());

        realtimeMessagingService.sendPrivateNotification(event.getRecipientId(), payload);
    }
}
