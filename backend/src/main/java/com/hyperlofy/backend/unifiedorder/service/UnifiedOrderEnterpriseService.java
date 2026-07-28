package com.hyperlofy.backend.unifiedorder.service;

import com.hyperlofy.backend.unifiedorder.entity.OrderInbox;
import com.hyperlofy.backend.unifiedorder.entity.OrderOutbox;
import com.hyperlofy.backend.unifiedorder.entity.OrderSaga;
import com.hyperlofy.backend.unifiedorder.repository.OrderInboxRepository;
import com.hyperlofy.backend.unifiedorder.repository.OrderOutboxRepository;
import com.hyperlofy.backend.unifiedorder.repository.OrderSagaRepository;
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
public class UnifiedOrderEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(UnifiedOrderEnterpriseService.class);

    private final OrderSagaRepository sagaRepository;
    private final OrderOutboxRepository outboxRepository;
    private final OrderInboxRepository inboxRepository;

    @Transactional
    public OrderSaga startSagaOrchestration(UUID orderId, String sagaName) {
        log.info("[UNIFIED ORDER ENTERPRISE] Starting Saga Orchestration OrderId={}, SagaName={}", orderId, sagaName);
        OrderSaga saga = OrderSaga.builder()
                .orderId(orderId)
                .sagaName(sagaName)
                .status("STARTED")
                .currentStep(1)
                .build();
        return sagaRepository.save(saga);
    }

    @Transactional
    public OrderOutbox publishToOutbox(UUID aggregateId, String eventType, String payload) {
        log.info("[UNIFIED ORDER ENTERPRISE] Writing event to Transactional Outbox AggregateId={}, EventType={}", aggregateId, eventType);
        OrderOutbox outbox = OrderOutbox.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .isPublished(false)
                .build();
        return outboxRepository.save(outbox);
    }

    @Transactional
    public boolean processInboxMessage(String messageId, String sourceService, String eventType) {
        Optional<OrderInbox> existing = inboxRepository.findByMessageId(messageId);
        if (existing.isPresent()) {
            log.warn("[UNIFIED ORDER ENTERPRISE] Duplicate message detected in Inbox. MessageId={}", messageId);
            return false;
        }

        log.info("[UNIFIED ORDER ENTERPRISE] Processing new Inbox message MessageId={}, Source={}, EventType={}", messageId, sourceService, eventType);
        OrderInbox inbox = OrderInbox.builder()
                .messageId(messageId)
                .sourceService(sourceService)
                .eventType(eventType)
                .isProcessed(true)
                .processedAt(ZonedDateTime.now())
                .build();
        inboxRepository.save(inbox);
        return true;
    }

    @Transactional
    public void markOutboxPublished(UUID outboxId) {
        outboxRepository.findById(outboxId).ifPresent(out -> {
            out.setIsPublished(true);
            out.setPublishedAt(ZonedDateTime.now());
            outboxRepository.save(out);
        });
    }

    @Transactional(readOnly = true)
    public List<OrderOutbox> getPendingOutboxEvents() {
        return outboxRepository.findByIsPublishedFalseOrderByCreatedAtAsc();
    }
}
