package com.hyperlofy.backend.security.service;

import com.hyperlofy.backend.security.entity.*;
import com.hyperlofy.backend.security.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Phase1EnterpriseAuthAddendumService {

    private static final Logger log = LoggerFactory.getLogger(Phase1EnterpriseAuthAddendumService.class);

    private final TrustedDeviceRepository deviceRepository;
    private final SecurityEventRepository securityEventRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final EmailVerificationRepository verificationRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @Transactional
    public TrustedDevice registerTrustedDevice(UUID userId, String name, String fingerprint, String os, String browser) {
        log.info("Registering trusted device for userId={}, fingerprint={}", userId, fingerprint);
        return deviceRepository.findByUserIdAndDeviceFingerprint(userId, fingerprint)
                .orElseGet(() -> deviceRepository.save(
                        TrustedDevice.builder()
                                .userId(userId)
                                .deviceName(name)
                                .deviceFingerprint(fingerprint)
                                .operatingSystem(os)
                                .browserName(browser)
                                .isTrusted(true)
                                .build()
                ));
    }

    @Transactional
    public SecurityEvent recordSecurityEvent(UUID userId, String type, String ip, String userAgent, double riskScore, String details) {
        log.info("Recording security event: userId={}, type={}, risk={}", userId, type, riskScore);
        SecurityEvent event = SecurityEvent.builder()
                .userId(userId)
                .eventType(type)
                .ipAddress(ip)
                .userAgent(userAgent)
                .riskScore(riskScore)
                .details(details)
                .build();
        return securityEventRepository.save(event);
    }

    @Transactional
    public PasswordResetToken createPasswordResetToken(UUID userId) {
        String token = UUID.randomUUID().toString();
        log.info("Created password reset token for userId={}", userId);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(userId)
                .resetToken(token)
                .isUsed(false)
                .expiresAt(ZonedDateTime.now().plusHours(1))
                .build();
        return resetTokenRepository.save(resetToken);
    }

    @Transactional(readOnly = true)
    public List<TrustedDevice> getUserTrustedDevices(UUID userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserPreference getUserPreferences(UUID userId) {
        return userPreferenceRepository.findByUserId(userId).orElseGet(() ->
                UserPreference.builder().userId(userId).build()
        );
    }
}
