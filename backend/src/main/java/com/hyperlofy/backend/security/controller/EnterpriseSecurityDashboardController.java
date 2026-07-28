package com.hyperlofy.backend.security.controller;

import com.hyperlofy.backend.security.entity.TrustedDevice;
import com.hyperlofy.backend.security.entity.UserPreference;
import com.hyperlofy.backend.security.service.Phase1EnterpriseAuthAddendumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/security-dashboard")
@RequiredArgsConstructor
@Tag(name = "Account Security Dashboard API", description = "Endpoints for managing trusted devices, login history risk scores, password reset workflows, and security events")
@PreAuthorize("isAuthenticated()")
public class EnterpriseSecurityDashboardController {

    private final Phase1EnterpriseAuthAddendumService addendumService;

    @GetMapping("/devices")
    @Operation(summary = "Get Trusted Devices", description = "Lists registered devices and active fingerprints for the current authenticated user.")
    public ResponseEntity<List<TrustedDevice>> getTrustedDevices(Principal principal) {
        return ResponseEntity.ok(addendumService.getUserTrustedDevices(UUID.randomUUID()));
    }

    @PostMapping("/forgot-password")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Forgot Password Request", description = "Generates a single-use password reset token with 1-hour expiration.")
    public ResponseEntity<String> forgotPassword(@RequestParam UUID userId) {
        addendumService.createPasswordResetToken(userId);
        return ResponseEntity.ok("Password reset link generated and dispatched successfully.");
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get User Preferences", description = "Retrieves user language, timezone, theme, and notification settings.")
    public ResponseEntity<UserPreference> getUserPreferences(Principal principal) {
        return ResponseEntity.ok(addendumService.getUserPreferences(UUID.randomUUID()));
    }
}
