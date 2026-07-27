package com.hyperlofy.backend.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class NotificationRequestedEvent extends ApplicationEvent {

    private final UUID recipientId;
    private final String title;
    private final String message;
    private final String notificationType; // PUSH, EMAIL, SMS, IN_APP

    public NotificationRequestedEvent(Object source, UUID recipientId, String title, String message, String notificationType) {
        super(source);
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }
}
