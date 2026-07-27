package com.hyperlofy.backend.common.health;

import com.hyperlofy.backend.event.service.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HyperlofyHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebSocketSessionManager sessionManager;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        // 1. DB Health Check
        boolean dbHealthy = false;
        try (Connection conn = dataSource.getConnection()) {
            dbHealthy = conn.isValid(2);
        } catch (Exception e) {
            details.put("databaseError", e.getMessage());
        }
        details.put("database", dbHealthy ? "UP" : "DOWN");

        // 2. Redis Health Check
        boolean redisHealthy = false;
        try {
            String ping = redisTemplate.getConnectionFactory().getConnection().ping();
            redisHealthy = "PONG".equalsIgnoreCase(ping);
        } catch (Exception e) {
            details.put("redisError", e.getMessage());
        }
        details.put("redis", redisHealthy ? "UP" : "DOWN");

        // 3. Active Sessions Metrics
        details.put("activeWebSocketUsers", sessionManager.getActiveUsers().size());
        details.put("activeWebSocketMerchants", sessionManager.getActiveMerchants().size());
        details.put("activeWebSocketAgents", sessionManager.getActiveAgents().size());

        // 4. Memory Usage
        long memoryMax = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        details.put("jvmMemoryUsedMb", memoryUsed);
        details.put("jvmMemoryMaxMb", memoryMax);

        if (dbHealthy && redisHealthy) {
            return Health.up().withDetails(details).build();
        } else {
            return Health.down().withDetails(details).build();
        }
    }
}
