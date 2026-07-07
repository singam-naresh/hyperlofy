package com.hyperlofy.backend.security.controller;

import com.hyperlofy.backend.security.entity.SecurityEvent;
import com.hyperlofy.backend.security.repository.SecurityEventRepository;
import com.hyperlofy.backend.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class SecurityController {

    private final SecurityService securityService;
    private final SecurityEventRepository securityEventRepository;

    @PostMapping("/rotate")
    public ResponseEntity<String> rotateToken(
            @RequestParam String oldRefreshToken,
            @RequestParam String deviceFingerprint,
            @RequestParam String ipAddress) {
        String newRefreshToken = securityService.rotateRefreshToken(oldRefreshToken, deviceFingerprint, ipAddress);
        return ResponseEntity.ok(newRefreshToken);
    }

    @PostMapping("/blacklist-ip")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<String> blacklistIp(@RequestParam String ipAddress) {
        securityService.blacklistIp(ipAddress);
        return ResponseEntity.ok("SUCCEEDED: Malicious IP blacklisted and locked permanently from accessing services.");
    }

    @GetMapping("/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<SecurityEvent>> getSecurityEvents() {
        return ResponseEntity.ok(securityEventRepository.findAll());
    }
}
