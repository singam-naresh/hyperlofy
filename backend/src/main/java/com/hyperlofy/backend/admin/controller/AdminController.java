package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminDto;
import com.hyperlofy.backend.agent.dto.AgentDto;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import com.hyperlofy.backend.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/agents")
    public ResponseEntity<List<AgentDto.ProfileResponse>> getAgentsByStatus(
            @RequestParam(name = "status", defaultValue = "PENDING") VerificationStatus status) {
        return ResponseEntity.ok(adminService.getAgentsByStatus(status));
    }

    @PostMapping("/agents/{agentProfileId}/approve")
    public ResponseEntity<AgentDto.ProfileResponse> approveAgent(
            Principal principal,
            @PathVariable UUID agentProfileId,
            @Valid @RequestBody AdminDto.ApproveAgentRequest request) {
        return ResponseEntity.ok(adminService.approveAgent(principal.getName(), agentProfileId, request));
    }

    @PostMapping("/agents/{agentProfileId}/reject")
    public ResponseEntity<AgentDto.ProfileResponse> rejectAgent(
            Principal principal,
            @PathVariable UUID agentProfileId,
            @Valid @RequestBody AdminDto.RejectAgentRequest request) {
        return ResponseEntity.ok(adminService.rejectAgent(principal.getName(), agentProfileId, request));
    }

    @PostMapping("/agents/{agentProfileId}/suspend")
    public ResponseEntity<AgentDto.ProfileResponse> suspendAgent(
            Principal principal,
            @PathVariable UUID agentProfileId,
            @Valid @RequestBody AdminDto.SuspendAgentRequest request) {
        return ResponseEntity.ok(adminService.suspendAgent(principal.getName(), agentProfileId, request));
    }

    @GetMapping("/agents/{agentProfileId}/logs")
    public ResponseEntity<List<AdminDto.VerificationLogResponse>> getAgentLogs(@PathVariable UUID agentProfileId) {
        return ResponseEntity.ok(adminService.getAgentVerificationLogs(agentProfileId));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminDto.SystemStatsResponse> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
    }
}
