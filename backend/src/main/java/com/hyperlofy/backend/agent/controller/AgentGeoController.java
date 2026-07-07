package com.hyperlofy.backend.agent.controller;

import com.hyperlofy.backend.agent.service.AgentGeoService;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/geo")
@RequiredArgsConstructor
public class AgentGeoController {

    private final AgentGeoService agentGeoService;
    private final UserRepository userRepository;

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        User agent = getCurrentAuthenticatedUser();
        agentGeoService.updateAgentLocation(agent.getId(), latitude, longitude);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/status")
    public ResponseEntity<Void> setOnlineStatus(@RequestParam boolean isOnline) {
        User agent = getCurrentAuthenticatedUser();
        agentGeoService.setAgentOnlineStatus(agent.getId(), isOnline);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<UUID>> getNearby(@RequestParam double latitude, @RequestParam double longitude, @RequestParam double radius) {
        return ResponseEntity.ok(agentGeoService.findNearbyAgents(latitude, longitude, radius));
    }

    @GetMapping("/heatmap")
    public ResponseEntity<Map<String, String>> getHeatmap() {
        return ResponseEntity.ok(agentGeoService.getAgentGeoHashes());
    }

    private User getCurrentAuthenticatedUser() {
        org.springframework.security.core.Authentication auth = 
                SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.UNAUTHORIZED));
    }
}
