package com.hyperlofy.backend.agent.service;

import com.hyperlofy.backend.agent.entity.AgentLocation;
import com.hyperlofy.backend.agent.repository.AgentLocationRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentGeoService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AgentLocationRepository agentLocationRepository;

    private static final String REDIS_GEO_KEY = "ACTIVE_AGENTS_GEO";
    private static final String STATUS_PREFIX = "AGENT_AVAILABILITY:";

    /**
     * 1. Live Agent Location Service: Updates coordinates in Redis and appends to Postgres audit history.
     */
    @Transactional
    public void updateAgentLocation(UUID agentId, double latitude, double longitude) {
        // Enforce Redis GEOADD
        redisTemplate.opsForGeo().add(
                REDIS_GEO_KEY,
                new Point(longitude, latitude), // Point(X: longitude, Y: latitude)
                agentId.toString()
        );

        // Keep availability active for 15 minutes by default on location ping
        redisTemplate.opsForValue().set(STATUS_PREFIX + agentId, "ONLINE", 15, TimeUnit.MINUTES);

        // Append tracking history asynchronously/persistently to PostgreSQL
        AgentLocation hist = AgentLocation.builder()
                .agentId(agentId)
                .latitude(latitude)
                .longitude(longitude)
                .accuracy(1.0)
                .bearing(0.0)
                .build();
        agentLocationRepository.save(hist);

        log.debug("[Redis GEO Entry Set] Agent: {}, Lat: {}, Lng: {}", agentId, latitude, longitude);
    }

    /**
     * 2. Agent Discovery Service: Manage dynamic online status.
     */
    public void setAgentOnlineStatus(UUID agentId, boolean isOnline) {
        if (isOnline) {
            redisTemplate.opsForValue().set(STATUS_PREFIX + agentId, "ONLINE");
        } else {
            // Remove from Redis GEO index
            redisTemplate.opsForGeo().remove(REDIS_GEO_KEY, agentId.toString());
            redisTemplate.delete(STATUS_PREFIX + agentId);
        }
        log.info("[Agent Discovery] Agent {} online state updated to: {}", agentId, isOnline);
    }

    /**
     * 3. Nearby Agent Engine: Filters and retrieves nearby candidates from Redis without query scanning PostgreSQL.
     */
    public List<UUID> findNearbyAgents(double latitude, double longitude, double radiusKm) {
        Point center = new Point(longitude, latitude);
        Distance dist = new Distance(radiusKm, RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(center, dist);

        // Redis GEORADIUS query search
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius(REDIS_GEO_KEY, circle);

        if (results == null) {
            return Collections.emptyList();
        }

        List<UUID> activeNearbyAgents = new ArrayList<>();
        results.forEach(res -> {
            String agentIdStr = (String) res.getContent().getName();
            if (agentIdStr != null) {
                UUID agentId = UUID.fromString(agentIdStr);
                // Verify if agent is actively ONLINE in Redis status cache
                String status = (String) redisTemplate.opsForValue().get(STATUS_PREFIX + agentId);
                if ("ONLINE".equals(status)) {
                    activeNearbyAgents.add(agentId);
                }
            }
        });

        log.info("[GEOSEARCH Engine] Nearby agents scan completed at ({}, {}). Candidates found: {}", 
                latitude, longitude, activeNearbyAgents.size());
        return activeNearbyAgents;
    }

    /**
     * 4. Agent Heatmap Service: Retrieves exact location geohashes and positions for load density clusters.
     */
    public Map<String, String> getAgentGeoHashes() {
        // Enforce Redis GEOHASH extraction
        Set<Object> members = redisTemplate.opsForZSet().range(REDIS_GEO_KEY, 0, -1);
        if (members == null || members.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> memberStrings = members.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> geohashes = redisTemplate.opsForGeo().hash(REDIS_GEO_KEY, memberStrings.toArray());
        
        Map<String, String> heatmap = new HashMap<>();
        for (int i = 0; i < memberStrings.size(); i++) {
            if (geohashes != null && i < geohashes.size()) {
                heatmap.put(memberStrings.get(i), geohashes.get(i));
            }
        }
        return heatmap;
    }
}
