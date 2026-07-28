package com.hyperlofy.backend.unifiedorder.controller;

import com.hyperlofy.backend.unifiedorder.entity.OrderOutbox;
import com.hyperlofy.backend.unifiedorder.entity.OrderSaga;
import com.hyperlofy.backend.unifiedorder.service.UnifiedOrderEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/unified-orders/enterprise")
@RequiredArgsConstructor
@Tag(name = "Unified Order Enterprise Addendum API", description = "Endpoints for Saga orchestration, Transactional Outbox/Inbox management, event replay, and compensation workflows")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class UnifiedOrderEnterpriseController {

    private final UnifiedOrderEnterpriseService enterpriseService;

    @PostMapping("/orders/{orderId}/sagas/start")
    @Operation(summary = "Start Order Saga Orchestration", description = "Initiates a multi-step distributed Saga workflow for order placement, matching, and payment authorization.")
    public ResponseEntity<OrderSaga> startSaga(@PathVariable UUID orderId, @RequestParam String sagaName) {
        return ResponseEntity.ok(enterpriseService.startSagaOrchestration(orderId, sagaName));
    }

    @PostMapping("/outbox")
    @Operation(summary = "Write Event to Transactional Outbox", description = "Atomically persists domain events into outbox table for reliable broker publishing.")
    public ResponseEntity<OrderOutbox> writeOutbox(@RequestParam UUID aggregateId, @RequestParam String eventType, @RequestBody String payload) {
        return ResponseEntity.ok(enterpriseService.publishToOutbox(aggregateId, eventType, payload));
    }

    @PostMapping("/inbox/process")
    @Operation(summary = "Process Inbox Message", description = "Validates message ID idempotency in inbox table to guarantee exactly-once event processing.")
    public ResponseEntity<Map<String, Object>> processInbox(@RequestParam String messageId, @RequestParam String sourceService, @RequestParam String eventType) {
        boolean processed = enterpriseService.processInboxMessage(messageId, sourceService, eventType);
        return ResponseEntity.ok(Map.of("messageId", messageId, "isNewMessageProcessed", processed));
    }

    @GetMapping("/outbox/pending")
    @Operation(summary = "Get Pending Outbox Events", description = "Returns unpublished domain events from outbox table for background publisher relay.")
    public ResponseEntity<List<OrderOutbox>> getPendingOutbox() {
        return ResponseEntity.ok(enterpriseService.getPendingOutboxEvents());
    }
}
