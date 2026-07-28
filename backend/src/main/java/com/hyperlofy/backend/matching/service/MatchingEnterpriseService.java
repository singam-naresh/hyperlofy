package com.hyperlofy.backend.matching.service;

import com.hyperlofy.backend.matching.entity.MatchingFairness;
import com.hyperlofy.backend.matching.entity.MatchingReservation;
import com.hyperlofy.backend.matching.entity.MatchingSurgeZone;
import com.hyperlofy.backend.matching.repository.MatchingFairnessRepository;
import com.hyperlofy.backend.matching.repository.MatchingReservationRepository;
import com.hyperlofy.backend.matching.repository.MatchingSurgeZoneRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(MatchingEnterpriseService.class);

    private final MatchingReservationRepository reservationRepository;
    private final MatchingSurgeZoneRepository surgeRepository;
    private final MatchingFairnessRepository fairnessRepository;

    @Transactional
    public MatchingReservation reserveDriverForScheduledOrder(UUID orderId, UUID driverId, ZonedDateTime scheduledTime) {
        log.info("[MATCHING ENTERPRISE] Pre-reserving DriverId={} for scheduled OrderId={} at {}", driverId, orderId, scheduledTime);

        MatchingReservation res = MatchingReservation.builder()
                .orderId(orderId)
                .reservedDriverId(driverId)
                .scheduledTime(scheduledTime)
                .status("RESERVED")
                .expiresAt(scheduledTime.minusMinutes(15))
                .build();

        return reservationRepository.save(res);
    }

    @Transactional
    public MatchingSurgeZone updateSurgeZoneDemand(String zoneName, Double demand, Double supply) {
        double multiplier = Math.max(1.0, demand / Math.max(0.1, supply));
        log.info("[MATCHING ENTERPRISE] Updating Surge Zone={} Demand={}, Supply={}, Multiplier={}", zoneName, demand, supply, String.format("%.2f", multiplier));

        MatchingSurgeZone zone = surgeRepository.findByZoneName(zoneName).orElseGet(() ->
                MatchingSurgeZone.builder().zoneName(zoneName).build()
        );

        zone.setDemandLevel(demand);
        zone.setSupplyLevel(supply);
        zone.setSurgeMultiplier(multiplier);
        zone.setIsActive(multiplier > 1.0);

        return surgeRepository.save(zone);
    }

    @Transactional(readOnly = true)
    public MatchingFairness getDriverFairnessScore(UUID driverId) {
        return fairnessRepository.findByDriverId(driverId).orElseGet(() ->
                MatchingFairness.builder()
                        .driverId(driverId)
                        .totalAssignments(0)
                        .totalWorkingHours(0.0)
                        .acceptanceRate(100.0)
                        .fairnessScore(95.0)
                        .build()
        );
    }
}
