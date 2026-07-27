package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.SupportTicket;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Platform Support Ticket API", description = "Endpoints for customer, merchant, and delivery support tickets, assignment, and escalation")
public class SupportTicketController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "List Support Tickets", description = "Retrieves all support tickets.")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(platformService.getAllTickets());
    }

    @PostMapping
    @Operation(summary = "Create Support Ticket", description = "Creates a new support ticket.")
    public ResponseEntity<SupportTicket> createTicket(@Valid @RequestBody SupportTicket ticket) {
        return ResponseEntity.ok(platformService.createTicket(ticket));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update Support Ticket Status", description = "Updates ticket status, resolution notes, or assigned admin.")
    public ResponseEntity<SupportTicket> updateTicket(
            @PathVariable UUID id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) UUID adminId) {

        return ResponseEntity.ok(platformService.updateTicket(id, status, notes, adminId));
    }
}
