package com.hyperlofy.backend.zone.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.zone.dto.*;
import com.hyperlofy.backend.zone.entity.PricingSlab;
import com.hyperlofy.backend.zone.entity.Zone;
import com.hyperlofy.backend.zone.repository.PricingSlabRepository;
import com.hyperlofy.backend.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;
    private final PricingSlabRepository pricingSlabRepository;
    private final GeoLocationService geoLocationService;

    @Transactional
    @CacheEvict(value = {"zones", "active_zones"}, allEntries = true)
    public ZoneResponse createZone(ZoneRequest request) {
        if (zoneRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new BusinessException("Zone with name " + request.getName() + " already exists", HttpStatus.CONFLICT);
        }

        Zone zone = Zone.builder()
                .name(request.getName())
                .centerLatitude(request.getCenterLatitude())
                .centerLongitude(request.getCenterLongitude())
                .radiusKm(request.getRadiusKm())
                .active(true)
                .build();

        Zone saved = zoneRepository.save(zone);
        log.info("Successfully created zone: {}", saved.getName());
        return mapToZoneResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "zones", key = "#id")
    public ZoneResponse getZoneById(UUID id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Zone not found with ID: " + id, HttpStatus.NOT_FOUND));
        return mapToZoneResponse(zone);
    }

    @Transactional(readOnly = true)
    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::mapToZoneResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "active_zones")
    public List<ZoneResponse> getAllActiveZones() {
        return zoneRepository.findByActiveTrue().stream()
                .map(this::mapToZoneResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"zones", "active_zones"}, allEntries = true)
    public ZoneResponse updateZone(UUID id, ZoneRequest request) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));

        zoneRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Zone name already in use", HttpStatus.CONFLICT);
                    }
                });

        zone.setName(request.getName());
        zone.setCenterLatitude(request.getCenterLatitude());
        zone.setCenterLongitude(request.getCenterLongitude());
        zone.setRadiusKm(request.getRadiusKm());

        Zone updated = zoneRepository.save(zone);
        log.info("Successfully updated zone: {}", updated.getName());
        return mapToZoneResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"zones", "active_zones"}, allEntries = true)
    public ZoneResponse enableZone(UUID id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));
        zone.setActive(true);
        Zone updated = zoneRepository.save(zone);
        log.info("Zone enabled: {}", updated.getName());
        return mapToZoneResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"zones", "active_zones"}, allEntries = true)
    public ZoneResponse disableZone(UUID id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));
        zone.setActive(false);
        Zone updated = zoneRepository.save(zone);
        log.info("Zone disabled: {}", updated.getName());
        return mapToZoneResponse(updated);
    }

    @Transactional
    @CacheEvict(value = {"zones", "active_zones"}, allEntries = true)
    public void deleteZone(UUID id) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));
        zoneRepository.delete(zone);
        log.info("Deleted zone with ID: {}", id);
    }

    // --- Pricing Slabs Business Logic ---

    @Transactional(readOnly = true)
    @Cacheable(value = "pricing_slabs", key = "#zoneId")
    public List<PricingSlabResponse> getPricingSlabsForZone(UUID zoneId) {
        // Assert zone exists
        if (!zoneRepository.existsById(zoneId)) {
            throw new BusinessException("Zone not found", HttpStatus.NOT_FOUND);
        }

        return pricingSlabRepository.findByZoneIdOrderByMinDistanceKmAsc(zoneId).stream()
                .map(this::mapToSlabResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "pricing_slabs", key = "#request.zoneId")
    public PricingSlabResponse addPricingSlab(PricingSlabRequest request) {
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));

        PricingSlab slab = PricingSlab.builder()
                .zone(zone)
                .minDistanceKm(request.getMinDistanceKm())
                .maxDistanceKm(request.getMaxDistanceKm())
                .basePrice(request.getBasePrice())
                .perKmPrice(request.getPerKmPrice())
                .build();

        PricingSlab saved = pricingSlabRepository.save(slab);
        log.info("Added pricing slab [{} - {} km] for zone: {}", saved.getMinDistanceKm(), saved.getMaxDistanceKm(), zone.getName());
        return mapToSlabResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "pricing_slabs", allEntries = true)
    public void deletePricingSlab(UUID slabId) {
        PricingSlab slab = pricingSlabRepository.findById(slabId)
                .orElseThrow(() -> new BusinessException("Pricing slab not found", HttpStatus.NOT_FOUND));
        pricingSlabRepository.delete(slab);
        log.info("Deleted pricing slab with ID: {}", slabId);
    }

    // --- Dynamic Delivery Fee Calculation Engine ---

    @Transactional(readOnly = true)
    public DeliveryFeeCalculationResponse calculateDeliveryFee(DeliveryFeeCalculationRequest request) {
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Zone not found", HttpStatus.NOT_FOUND));

        if (!zone.isActive()) {
            throw new BusinessException("Requested delivery zone is currently disabled", HttpStatus.BAD_REQUEST);
        }

        // 1. Verify if destination is within zone boundaries
        boolean matchesZone = geoLocationService.isWithinRadius(
                request.getDeliveryLatitude(),
                request.getDeliveryLongitude(),
                zone.getCenterLatitude(),
                zone.getCenterLongitude(),
                zone.getRadiusKm()
        );

        // 2. Distance from Store Center to Customer Delivery Location
        double distanceKm = geoLocationService.calculateDistanceKm(
                request.getStoreLatitude(),
                request.getStoreLongitude(),
                request.getDeliveryLatitude(),
                request.getDeliveryLongitude()
        );

        // 3. Find matching slab for distance
        List<PricingSlab> slabs = pricingSlabRepository.findByZoneIdOrderByMinDistanceKmAsc(zone.getId());
        PricingSlab activeSlab = slabs.stream()
                .filter(s -> distanceKm >= s.getMinDistanceKm() && distanceKm <= s.getMaxDistanceKm())
                .findFirst()
                .orElse(null);

        BigDecimal finalFee;

        if (activeSlab != null) {
            BigDecimal base = activeSlab.getBasePrice();
            BigDecimal perKmMultiplier = activeSlab.getPerKmPrice();
            double extraDistance = distanceKm - activeSlab.getMinDistanceKm();
            if (extraDistance < 0) extraDistance = 0;

            finalFee = base.add(perKmMultiplier.multiply(BigDecimal.valueOf(extraDistance)));
        } else {
            // Default pricing model fallback (e.g. baseline flat fee)
            finalFee = BigDecimal.valueOf(15.0).add(BigDecimal.valueOf(10.0 * distanceKm));
        }

        // Scale to 2 decimal places cleanly
        finalFee = finalFee.setScale(2, RoundingMode.HALF_UP);

        return DeliveryFeeCalculationResponse.builder()
                .distanceKm(BigDecimal.valueOf(distanceKm).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .deliveryFee(finalFee)
                .zoneId(zone.getId())
                .zoneName(zone.getName())
                .withinZoneBounds(matchesZone)
                .build();
    }

    // --- Helper Mappers ---

    private ZoneResponse mapToZoneResponse(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .centerLatitude(zone.getCenterLatitude())
                .centerLongitude(zone.getCenterLongitude())
                .radiusKm(zone.getRadiusKm())
                .active(zone.isActive())
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }

    private PricingSlabResponse mapToSlabResponse(PricingSlab slab) {
        return PricingSlabResponse.builder()
                .id(slab.getId())
                .zoneId(slab.getZone().getId())
                .minDistanceKm(slab.getMinDistanceKm())
                .maxDistanceKm(slab.getMaxDistanceKm())
                .basePrice(slab.getBasePrice())
                .perKmPrice(slab.getPerKmPrice())
                .build();
    }
}
