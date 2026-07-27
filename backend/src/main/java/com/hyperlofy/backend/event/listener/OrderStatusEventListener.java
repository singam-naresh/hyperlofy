package com.hyperlofy.backend.event.listener;

import com.hyperlofy.backend.event.domain.OrderCreatedEvent;
import com.hyperlofy.backend.event.domain.OrderStatusChangedEvent;
import com.hyperlofy.backend.event.dto.OrderStatusEventDTO;
import com.hyperlofy.backend.event.service.RealtimeMessagingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusEventListener.class);
    private final RealtimeMessagingService realtimeMessagingService;

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Async handling OrderCreatedEvent for orderId={}", event.getOrderId());
        OrderStatusEventDTO dto = OrderStatusEventDTO.builder()
                .orderId(event.getOrderId())
                .previousStatus("NONE")
                .newStatus("CREATED")
                .customerId(event.getCustomerId())
                .merchantId(event.getMerchantId())
                .build();

        realtimeMessagingService.broadcastOrderStatus(dto);
    }

    @Async
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Async handling OrderStatusChangedEvent for orderId={}, status {} -> {}", event.getOrderId(), event.getPreviousStatus(), event.getNewStatus());
        OrderStatusEventDTO dto = OrderStatusEventDTO.builder()
                .orderId(event.getOrderId())
                .previousStatus(event.getPreviousStatus())
                .newStatus(event.getNewStatus())
                .customerId(event.getCustomerId())
                .merchantId(event.getMerchantId())
                .agentId(event.getAgentId())
                .build();

        realtimeMessagingService.broadcastOrderStatus(dto);
    }
}
