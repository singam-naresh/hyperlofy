package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.SupportTicketMessage;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/support/chat")
@RequiredArgsConstructor
@Tag(name = "Live Customer Support Chat API", description = "Real-time Customer-Agent-AI chat messages, internal supervisor notes, and attachment media sharing")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SupportChatController {

    private final CustomerSupportService supportService;

    @PostMapping("/message")
    @Operation(summary = "Send Live Support Chat Message / Internal Note", description = "Sends real-time message or internal supervisor note to ticket conversation thread.")
    public ResponseEntity<SupportTicketMessage> sendMessage(
            @RequestParam UUID ticketId,
            @RequestParam UUID senderUserId,
            @RequestParam(required = false) String senderRole,
            @RequestParam String content,
            @RequestParam(required = false) Boolean isInternalNote,
            @RequestParam(required = false) String attachments) {
        return ResponseEntity.ok(supportService.sendMessage(ticketId, senderUserId, senderRole, content, isInternalNote, attachments));
    }

    @GetMapping("/history/{ticketId}")
    @Operation(summary = "Get Support Chat Conversation History", description = "Returns chronological message history and attachments for specified support ticket.")
    public ResponseEntity<List<SupportTicketMessage>> getHistory(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(supportService.getMessages(ticketId));
    }
}
