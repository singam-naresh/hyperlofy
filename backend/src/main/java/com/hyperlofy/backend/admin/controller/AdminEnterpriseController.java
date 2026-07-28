package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.entity.AdminSessionAudit;
import com.hyperlofy.backend.admin.entity.AdminTask;
import com.hyperlofy.backend.admin.entity.AdminWorkflow;
import com.hyperlofy.backend.admin.service.AdminEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/enterprise")
@RequiredArgsConstructor
@Tag(name = "Admin Platform Enterprise Addendum API", description = "Endpoints for Workflow Automation, Skill-based Workforce Routing, AI-Assisted Operations, and Admin Session Audit Governance")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AdminEnterpriseController {

    private final AdminEnterpriseService enterpriseService;

    @PostMapping("/workflow/start")
    @Operation(summary = "Start Automated Operations Workflow", description = "Triggers multi-step SLA-monitored operational workflow.")
    public ResponseEntity<AdminWorkflow> startWorkflow(
            @RequestParam String workflowName,
            @RequestParam String triggerEvent,
            @RequestParam String initialStep) {
        return ResponseEntity.ok(enterpriseService.startWorkflow(workflowName, triggerEvent, initialStep));
    }

    @PostMapping("/task/assign")
    @Operation(summary = "Skill-Based Task Assignment", description = "Assigns support ticket to optimal agent based on active workload capacity and skill category.")
    public ResponseEntity<AdminTask> assignTask(
            @RequestParam(required = false) UUID workflowId,
            @RequestParam String title,
            @RequestParam String agentUser,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(enterpriseService.assignSupportTask(workflowId, title, agentUser, priority));
    }

    @PostMapping("/session/audit")
    @Operation(summary = "Record Admin Session Audit Entry", description = "Logs administrative session access with IP address and privilege level validation.")
    public ResponseEntity<AdminSessionAudit> recordSession(
            @RequestParam String adminUser,
            @RequestParam String ipAddress,
            @RequestParam(required = false) String privilegeLevel) {
        return ResponseEntity.ok(enterpriseService.recordSessionAudit(adminUser, ipAddress, privilegeLevel));
    }
}
