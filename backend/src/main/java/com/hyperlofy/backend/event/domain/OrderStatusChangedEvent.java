package com.hyperlofy.backend.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {

    private final UUID orderId;
    private final String previousStatus;
    private final String newStatus;
    private final UUID customerId;
    private final UUID merchantId;
    private final UUID agentId;

    public OrderStatusChangedEvent(Object source, UUID orderId, String previousStatus, String newStatus, UUID customerId, UUID merchantId, UUID agentId) {
        super(source);
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.agentId = agentId;
    }
}
