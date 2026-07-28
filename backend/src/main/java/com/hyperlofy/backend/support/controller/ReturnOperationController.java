package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.ReturnCase;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/returns")
@RequiredArgsConstructor
@Tag(name = "Customer Return Operations API", description = "Manage product pickup scheduling, warehouse inspection verification, and return approvals")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ReturnOperationController {

    private final CustomerSupportService supportService;

    @PostMapping
    @Operation(summary = "Request Product Return", description = "Schedules reverse logistics delivery pickup for damaged or wrong product items.")
    public ResponseEntity<ReturnCase> requestReturn(
            @RequestParam String returnCode,
            @RequestParam UUID ticketId,
            @RequestParam UUID orderId,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(supportService.requestReturn(returnCode, ticketId, orderId, tenantId));
    }
}
