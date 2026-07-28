package com.hyperlofy.backend.unifiedorder.service;

import com.hyperlofy.backend.unifiedorder.entity.MasterOrder;
import com.hyperlofy.backend.unifiedorder.entity.OrderIdempotency;
import com.hyperlofy.backend.unifiedorder.entity.OrderTimeline;
import com.hyperlofy.backend.unifiedorder.repository.MasterOrderRepository;
import com.hyperlofy.backend.unifiedorder.repository.OrderIdempotencyRepository;
import com.hyperlofy.backend.unifiedorder.repository.OrderTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnifiedOrderService {

    private static final Logger log = LoggerFactory.getLogger(UnifiedOrderService.class);

    private final MasterOrderRepository orderRepository;
    private final OrderTimelineRepository timelineRepository;
    private final OrderIdempotencyRepository idempotencyRepository;

    @Transactional
    public MasterOrder registerMasterOrder(String idempotencyKey, UUID businessOrderId, String orderType, UUID customerId, UUID merchantId, Double amount, String sourceService) {
        if (idempotencyKey != null) {
            Optional<OrderIdempotency> existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("[UNIFIED ORDER ENGINE] Idempotent request detected. Returning existing master order OrderId={}", existing.get().getOrderId());
                return getOrderById(existing.get().getOrderId());
            }
        }

        String globalNum = "ORD-HYP-" + System.currentTimeMillis();
        log.info("[UNIFIED ORDER ENGINE] Registering Master Order Num={}, Type={}, CustomerId={}, Amount={}", globalNum, orderType, customerId, amount);

        MasterOrder master = MasterOrder.builder()
                .globalOrderNumber(globalNum)
                .businessOrderId(businessOrderId)
                .orderType(orderType)
                .customerId(customerId)
                .merchantId(merchantId)
                .status("CREATED")
                .paymentStatus("PENDING")
                .pricingStatus("COMPLETED")
                .trackingStatus("NOT_STARTED")
                .priority("NORMAL")
                .totalAmount(amount)
                .sourceService(sourceService)
                .build();

        MasterOrder saved = orderRepository.save(master);

        if (idempotencyKey != null) {
            idempotencyRepository.save(OrderIdempotency.builder()
                    .idempotencyKey(idempotencyKey)
                    .orderId(saved.getId())
                    .build());
        }

        // Record initial timeline event
        addTimelineEvent(saved.getId(), "OrderCreated", customerId.toString(), "CUSTOMER", sourceService, "Master order registered successfully.");

        return saved;
    }

    @Transactional
    public OrderTimeline addTimelineEvent(UUID orderId, String eventName, String actorId, String actorType, String sourceService, String description) {
        log.info("[UNIFIED ORDER ENGINE] Timeline Event OrderId={}, Event={}, Actor={}", orderId, eventName, actorId);

        OrderTimeline timeline = OrderTimeline.builder()
                .orderId(orderId)
                .eventName(eventName)
                .actorId(actorId)
                .actorType(actorType)
                .sourceService(sourceService)
                .eventDescription(description)
                .eventTime(ZonedDateTime.now())
                .build();

        return timelineRepository.save(timeline);
    }

    @Transactional
    public MasterOrder updateOrderStatus(UUID orderId, String newStatus, String changeReason, String actorId, String actorType, String sourceService) {
        MasterOrder master = getOrderById(orderId);
        String oldStatus = master.getStatus();
        log.info("[UNIFIED ORDER ENGINE] Status Transition OrderId={}, From={}, To={}", orderId, oldStatus, newStatus);

        master.setStatus(newStatus);
        if ("COMPLETED".equals(newStatus)) {
            master.setCompletedAt(ZonedDateTime.now());
        } else if ("CANCELLED".equals(newStatus)) {
            master.setCancelledAt(ZonedDateTime.now());
        }

        MasterOrder updated = orderRepository.save(master);
        addTimelineEvent(orderId, "StatusChanged_" + newStatus, actorId, actorType, sourceService, "Transitioned from " + oldStatus + " to " + newStatus + ": " + changeReason);

        return updated;
    }

    @Transactional(readOnly = true)
    public MasterOrder getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("MasterOrder not found with id: " + orderId));
    }

    @Transactional(readOnly = true)
    public List<OrderTimeline> getOrderTimeline(UUID orderId) {
        return timelineRepository.findByOrderIdOrderByEventTimeAsc(orderId);
    }

    @Transactional(readOnly = true)
    public List<MasterOrder> getCustomerOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }
}
