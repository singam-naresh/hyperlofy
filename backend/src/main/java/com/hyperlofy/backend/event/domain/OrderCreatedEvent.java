package com.hyperlofy.backend.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    private final UUID orderId;
    private final UUID customerId;
    private final UUID merchantId;
    private final BigDecimal totalAmount;

    public OrderCreatedEvent(Object source, UUID orderId, UUID customerId, UUID merchantId, BigDecimal totalAmount) {
        super(source);
        this.orderId = orderId;
        this.customerId = customerId;
        this.merchantId = merchantId;
        this.totalAmount = totalAmount;
    }
}
