package com.hyperlofy.backend.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionManager.class);

    private final Set<UUID> activeUsers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> activeMerchants = ConcurrentHashMap.newKeySet();
    private final Set<UUID> activeAgents = ConcurrentHashMap.newKeySet();
    private final Set<UUID> activeAdmins = ConcurrentHashMap.newKeySet();

    public void registerUser(UUID userId, String role) {
        if (userId == null) return;
        if ("MERCHANT".equalsIgnoreCase(role)) {
            activeMerchants.add(userId);
        } else if ("AGENT".equalsIgnoreCase(role) || "DRIVER".equalsIgnoreCase(role)) {
            activeAgents.add(userId);
        } else if ("ADMIN".equalsIgnoreCase(role) || "SUPER_ADMIN".equalsIgnoreCase(role)) {
            activeAdmins.add(userId);
        } else {
            activeUsers.add(userId);
        }
        log.info("WebSocket session registered: userId={}, role={}", userId, role);
    }

    public void unregisterUser(UUID userId) {
        if (userId == null) return;
        activeUsers.remove(userId);
        activeMerchants.remove(userId);
        activeAgents.remove(userId);
        activeAdmins.remove(userId);
        log.info("WebSocket session unregistered: userId={}", userId);
    }

    public boolean isUserOnline(UUID userId) {
        return activeUsers.contains(userId) || activeMerchants.contains(userId)
                || activeAgents.contains(userId) || activeAdmins.contains(userId);
    }

    public Set<UUID> getActiveUsers() {
        return Collections.unmodifiableSet(activeUsers);
    }

    public Set<UUID> getActiveMerchants() {
        return Collections.unmodifiableSet(activeMerchants);
    }

    public Set<UUID> getActiveAgents() {
        return Collections.unmodifiableSet(activeAgents);
    }
}
