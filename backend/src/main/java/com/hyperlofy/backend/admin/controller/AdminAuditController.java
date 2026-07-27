package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.entity.AdminAuditLog;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@Tag(name = "Admin Audit Logging API", description = "Endpoints for inspecting administrative audit trail logs for suspensions, activations, blocks, and financial actions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminAuditController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "Get Admin Audit Logs", description = "Retrieves complete chronological audit log history of administrative actions.")
    public ResponseEntity<List<AdminAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminPlatformService.getAuditLogs());
    }
}
