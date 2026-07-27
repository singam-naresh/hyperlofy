package com.hyperlofy.backend.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class RefundCompletedEvent extends ApplicationEvent {

    private final UUID refundId;
    private final UUID orderId;
    private final UUID customerId;
    private final BigDecimal refundAmount;

    public RefundCompletedEvent(Object source, UUID refundId, UUID orderId, UUID customerId, BigDecimal refundAmount) {
        super(source);
        this.refundId = refundId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.refundAmount = refundAmount;
    }
}
