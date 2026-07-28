package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.entity.AdminCase;
import com.hyperlofy.backend.admin.service.AdminOperationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/operations")
@RequiredArgsConstructor
@Tag(name = "Admin Operations Support Case API", description = "Endpoints for customer support and merchant operations to manage support tickets, manual order reassignments, and refunds")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'CUSTOMER_SUPPORT')")
public class AdminOperationsController {

    private final AdminOperationsService adminService;

    @PostMapping("/cases/create")
    @Operation(summary = "Create Support Case", description = "Opens customer or merchant support ticket with SLA priority tracking.")
    public ResponseEntity<AdminCase> createCase(
            @RequestParam String subject,
            @RequestParam UUID customerId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(adminService.createSupportCase(subject, customerId, orderId, priority));
    }
}
