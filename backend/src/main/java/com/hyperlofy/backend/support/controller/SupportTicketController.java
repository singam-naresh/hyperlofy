package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.SupportTicket;
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
@RequestMapping("/api/v1/support/tickets")
@RequiredArgsConstructor
@Tag(name = "Customer Support Ticket Management API", description = "Create, assign, escalate, and resolve customer support tickets across all categories (Refund, Return, Missing Product, Wrong Product)")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SupportTicketController {

    private final CustomerSupportService supportService;

    @PostMapping
    @Operation(summary = "Create Support Ticket", description = "Creates a customer support ticket with SLA resolution timer and automatic priority assignment.")
    public ResponseEntity<SupportTicket> create(
            @RequestParam String ticketCode,
            @RequestParam UUID customerId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(supportService.createTicket(ticketCode, customerId, orderId, category, priority, tenantId));
    }

    @GetMapping
    @Operation(summary = "List Customer Support Tickets", description = "Returns support tickets for customer ID or support agent queue.")
    public ResponseEntity<List<SupportTicket>> getTickets(@RequestParam UUID customerId) {
        return ResponseEntity.ok(supportService.getTicketsByCustomer(customerId));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign Support Ticket to Agent", description = "Assigns ticket to a customer support agent or supervisor.")
    public ResponseEntity<SupportTicket> assign(@PathVariable UUID id, @RequestParam UUID agentId) {
        return ResponseEntity.ok(supportService.assignTicket(id, agentId));
    }

    @PostMapping("/{id}/escalate")
    @Operation(summary = "Escalate Support Ticket", description = "Escalates ticket priority to CRITICAL and routes to Operations Manager.")
    public ResponseEntity<SupportTicket> escalate(@PathVariable UUID id) {
        return ResponseEntity.ok(supportService.escalateTicket(id));
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve Support Ticket", description = "Marks ticket status as RESOLVED upon successful customer resolution.")
    public ResponseEntity<SupportTicket> resolve(@PathVariable UUID id) {
        return ResponseEntity.ok(supportService.resolveTicket(id));
    }
}
