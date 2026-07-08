package com.hyperlofy.backend.security.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.security.entity.DeviceSession;
import com.hyperlofy.backend.security.entity.LoginAudit;
import com.hyperlofy.backend.security.entity.SecurityEvent;
import com.hyperlofy.backend.security.repository.DeviceSessionRepository;
import com.hyperlofy.backend.security.repository.LoginAuditRepository;
import com.hyperlofy.backend.security.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final SecurityEventRepository securityEventRepository;
    private final LoginAuditRepository loginAuditRepository;
    private final DeviceSessionRepository deviceSessionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final String BRUTE_FORCE_PREFIX = "SECURITY_BRUTE_FORCE_LOCK:";
    private static final String REVOKED_JWT_PREFIX = "SECURITY_REVOKED_JWT:";
    private static final String IP_REPUTATION_PREFIX = "SECURITY_IP_BLACKLIST:";
    private static final String RATE_LIMIT_PREFIX = "SECURITY_RATE_LIMIT:";

    /**
     * 1. Refresh Token Rotation & Session Auditing
     */
    @Transactional
    public String rotateRefreshToken(String oldToken, String deviceFingerprint, String ipAddress) {
        DeviceSession session = deviceSessionRepository.findByRefreshToken(oldToken)
                .orElseThrow(() -> {
                    // Breach Attempt: Someone using an invalid refresh token is highly suspicious
                    logSecurityEvent(null, "UNKNOWN_THIEF", "INVALID_REFRESH_TOKEN_ATTEMPT", ipAddress, deviceFingerprint, "CRITICAL", 
                            "System detected attempt to rotate invalid refresh token. Potential leakage!");
                    return new BusinessException("Session is invalid or expired", HttpStatus.UNAUTHORIZED);
                });

        if (session.isRevoked() || session.getExpiresAt().isBefore(OffsetDateTime.now())) {
            // Re-use detection: if a token is rotated twice, immediately revoke all of that user's active sessions!
            List<DeviceSession> activeSessions = deviceSessionRepository.findByUserIdAndRevokedFalse(session.getUserId());
            activeSessions.forEach(s -> s.setRevoked(true));
            deviceSessionRepository.saveAll(activeSessions);

            logSecurityEvent(session.getUserId(), "USER_ID_" + session.getUserId(), "REFRESH_TOKEN_REUSE_DETECTION", ipAddress, deviceFingerprint, "CRITICAL", 
                    "Symptom of session token theft! Token reuse detected. Revoking all sessions for user immediately.");
            throw new BusinessException("Session state has expired due to rotation reuse breach.", HttpStatus.UNAUTHORIZED);
        }

        // 3. Device Fingerprint Traps: If fingerprint changes, flag anomaly and force full authentication retry
        if (!session.getDeviceFingerprint().equals(deviceFingerprint)) {
            session.setRevoked(true);
            deviceSessionRepository.save(session);

            logSecurityEvent(session.getUserId(), "USER_ID_" + session.getUserId(), "FINGERPRINT_HIJACK_ATTEMPT", ipAddress, deviceFingerprint, "HIGH", 
                    String.format("Hijack block! Fingerprint changed. Expected %s, received %s.", session.getDeviceFingerprint(), deviceFingerprint));
            throw new BusinessException("Security warning: device fingerprint mismatch. Re-authentication required.", HttpStatus.FORBIDDEN);
        }

        // Rotate the session refresh token
        session.setRevoked(true); // Old token is now consumed
        deviceSessionRepository.save(session);

        String newRefreshToken = UUID.randomUUID().toString();
        DeviceSession rotatedSession = DeviceSession.builder()
                .userId(session.getUserId())
                .refreshToken(newRefreshToken)
                .deviceFingerprint(deviceFingerprint)
                .ipAddress(ipAddress)
                .expiresAt(OffsetDateTime.now().plusDays(7))
                .build();
        deviceSessionRepository.save(rotatedSession);

        log.info("[Token Rotation Success] User: {}. Rotated token saved safely.", session.getUserId());
        return newRefreshToken;
    }

    /**
     * 2. JWT Revocation List
     */
    public void revokeJwtToken(String jwtToken, long expiryMs) {
        String key = REVOKED_JWT_PREFIX + jwtToken;
        redisTemplate.opsForValue().set(key, "revoked", expiryMs, TimeUnit.MILLISECONDS);
        log.info("[JWT Revocation Engine] Secured blacklist for token hash with expiry MS of: {}", expiryMs);
    }

    public boolean isJwtRevoked(String jwtToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_JWT_PREFIX + jwtToken));
    }

    /**
     * 4. Login Anomaly & Anomaly Geo/IP Checks
     */
    @Transactional
    public void evaluateLoginAnomalies(UUID userId, String email, String ipAddress, String deviceFingerprint) {
        // Fetch last successful login audits
        List<LoginAudit> recentSuccess = loginAuditRepository.findByEmailOrderByCreatedAtDesc(email);
        if (!recentSuccess.isEmpty()) {
            LoginAudit prior = recentSuccess.get(0);
            
            // Check if IP is completely different (subnet mismatch)
            String[] priorSub = prior.getIpAddress().split("\\.");
            String[] currentSub = ipAddress.split("\\.");
            if (priorSub.length > 2 && currentSub.length > 2) {
                if (!priorSub[0].equals(currentSub[0]) || !priorSub[1].equals(currentSub[1])) {
                    logSecurityEvent(userId, email, "IP_SUBNET_ANOMALY", ipAddress, deviceFingerprint, "MEDIUM", 
                            String.format("User logged in from remote subnet. Original: %s, Current: %s", prior.getIpAddress(), ipAddress));
                }
            }
        }
    }

    /**
     * 5. Brute Force Protection Engine
     */
    @Transactional
    public void handleFailedLogin(String email, String ipAddress, String deviceFingerprint, String reason) {
        LoginAudit audit = LoginAudit.builder()
                .email(email)
                .loginStatus("FAILED_CREDENTIALS")
                .ipAddress(ipAddress)
                .deviceFingerprint(deviceFingerprint)
                .failureReason(reason)
                .build();
        loginAuditRepository.save(audit);

        String lockKey = BRUTE_FORCE_PREFIX + email;
        Object val = redisTemplate.opsForValue().get(lockKey);
        int attempts = readCounterValue(val);
        attempts++;

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            // Lock account for 30 minutes in Redis
            redisTemplate.opsForValue().set(lockKey, attempts, 30, TimeUnit.MINUTES);
            logSecurityEvent(null, email, "BRUTE_FORCE_LOCK", ipAddress, deviceFingerprint, "HIGH", 
                    "Max failure threshold exceeded. Initiating account login freeze lock for 30 minutes.");
            throw new BusinessException("Your account is temporarily locked due to brute force protection. Try again in 30 minutes.", HttpStatus.LOCKED);
        } else {
            redisTemplate.opsForValue().set(lockKey, attempts, 10, TimeUnit.MINUTES);
        }
    }

    public void verifyBruteForceCheck(String email) {
        String lockKey = BRUTE_FORCE_PREFIX + email;
        Object val = redisTemplate.opsForValue().get(lockKey);
        if (readCounterValue(val) >= MAX_FAILED_ATTEMPTS) {
            throw new BusinessException("Security Lockout: Account is locked under brute force protection algorithms.", HttpStatus.LOCKED);
        }
    }

    @Transactional
    public void logSuccessfulLogin(UUID userId, String email, String ipAddress, String deviceFingerprint) {
        LoginAudit audit = LoginAudit.builder()
                .email(email)
                .loginStatus("SUCCESS")
                .ipAddress(ipAddress)
                .deviceFingerprint(deviceFingerprint)
                .build();
        loginAuditRepository.save(audit);

        // Clear brute force counters on successful auth
        redisTemplate.delete(BRUTE_FORCE_PREFIX + email);
    }

    /**
     * 6. Rate Limiting & Throttling
     */
    public void checkRateLimit(String clientIp, String endpoint) {
        String limitKey = RATE_LIMIT_PREFIX + clientIp + ":" + endpoint;
        Long hits = redisTemplate.opsForValue().increment(limitKey);
        
        if (hits != null && hits == 1) {
            redisTemplate.expire(limitKey, 60, TimeUnit.SECONDS); // Allow window limit reset in 60s
        }

        int maxAllowedHits = 100; // Limit: 100 calls per minute
        if (hits != null && hits > maxAllowedHits) {
            logSecurityEvent(null, "CLIENT_IP_" + clientIp, "RATE_LIMIT_EXCEEDED", clientIp, null, "LOW", 
                    "Throttling block: client IP exceeded standard boundary allowance of 100 hits/min.");
            throw new BusinessException("Too many requests - Rate limit exceeded. Try again shortly.", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    /**
     * 7. IP Reputation & Blacklist Safeguard Checks
     */
    public void evaluateIpReputation(String ipAddress) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(IP_REPUTATION_PREFIX + ipAddress))) {
            logSecurityEvent(null, "MALICIOUS_IP", "IP_REPUTATION_FAIL", ipAddress, null, "HIGH", 
                    "Anomalous system access blocked. Client IP belongs to known blacklisted subnets.");
            throw new BusinessException("Access Denied: Connections from proxy/or blacklisted subnets restricted.", HttpStatus.FORBIDDEN);
        }
    }

    public void blacklistIp(String ipAddress) {
        redisTemplate.opsForValue().set(IP_REPUTATION_PREFIX + ipAddress, "BLACK_LISTED", 30, TimeUnit.DAYS);
        log.info("[IP Blacklist Alert] Added malicious IP node payload to active block pool: {}", ipAddress);
    }

    /**
     * 8. Common Security Logger
     */
    @Transactional
    public void logSecurityEvent(UUID userId, String email, String eventType, String ip, String fingerprint, String severity, String description) {
        SecurityEvent event = SecurityEvent.builder()
                .userId(userId)
                .email(email)
                .eventType(eventType)
                .ipAddress(ip != null ? ip : "0.0.0.0")
                .deviceFingerprint(fingerprint != null ? fingerprint : "GENERIC_BROWSER")
                .severity(severity)
                .description(description)
                .build();
        securityEventRepository.save(event);
        log.warn("[SECURITY AUDIT ALERT] Severity: {}, Event: {}, Info: {}", severity, eventType, description);
    }

    private int readCounterValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }
}
