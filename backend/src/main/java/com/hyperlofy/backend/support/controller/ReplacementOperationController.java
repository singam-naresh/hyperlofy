package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.ReplacementCase;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/replacements")
@RequiredArgsConstructor
@Tag(name = "Customer Replacement Operations API", description = "Coordinate merchant inventory verification, replacement item dispatch, and delivery completion")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ReplacementOperationController {

    private final CustomerSupportService supportService;

    @PostMapping
    @Operation(summary = "Request Product Replacement", description = "Initiates merchant inventory allocation and replacement dispatch for defective items.")
    public ResponseEntity<ReplacementCase> requestReplacement(
            @RequestParam String replacementCode,
            @RequestParam UUID ticketId,
            @RequestParam UUID orderId,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(supportService.requestReplacement(replacementCode, ticketId, orderId, tenantId));
    }
}
