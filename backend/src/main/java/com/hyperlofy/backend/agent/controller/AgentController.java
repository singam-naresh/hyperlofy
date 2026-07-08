package com.hyperlofy.backend.agent.controller;

import com.hyperlofy.backend.agent.dto.AgentDto;
import com.hyperlofy.backend.agent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/agent")
@PreAuthorize("hasRole('AGENT')")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping("/profile")
    public ResponseEntity<AgentDto.ProfileResponse> getProfile(Principal principal) {
        return ResponseEntity.ok(agentService.getAgentProfile(principal.getName()));
    }

    @PutMapping("/location")
    public ResponseEntity<AgentDto.ProfileResponse> updateLocation(
            Principal principal, 
            @Valid @RequestBody AgentDto.LocationUpdateRequest request) {
        return ResponseEntity.ok(agentService.updateLocation(principal.getName(), request));
    }

    @PutMapping("/availability")
    public ResponseEntity<AgentDto.ProfileResponse> updateAvailability(
            Principal principal, 
            @Valid @RequestBody AgentDto.UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(agentService.updateAvailability(principal.getName(), request.getAvailable()));
    }

    @PutMapping("/documents")
    public ResponseEntity<AgentDto.ProfileResponse> uploadDocuments(
            Principal principal, 
            @Valid @RequestBody AgentDto.UploadDocumentsRequest request) {
        return ResponseEntity.ok(agentService.uploadDocuments(principal.getName(), request));
    }
}
