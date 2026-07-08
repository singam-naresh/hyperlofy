package com.hyperlofy.backend.zone.controller;

import com.hyperlofy.backend.zone.dto.PricingSlabRequest;
import com.hyperlofy.backend.zone.dto.PricingSlabResponse;
import com.hyperlofy.backend.zone.dto.ZoneRequest;
import com.hyperlofy.backend.zone.dto.ZoneResponse;
import com.hyperlofy.backend.zone.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/zones")
@RequiredArgsConstructor
public class AdminZoneController {

    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody ZoneRequest request) {
        return new ResponseEntity<>(zoneService.createZone(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneResponse> updateZone(@PathVariable UUID id, @Valid @RequestBody ZoneRequest request) {
        return ResponseEntity.ok(zoneService.updateZone(id, request));
    }

    @GetMapping
    public ResponseEntity<List<ZoneResponse>> getAllZones() {
        return ResponseEntity.ok(zoneService.getAllZones());
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<ZoneResponse> enableZone(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.enableZone(id));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<ZoneResponse> disableZone(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.disableZone(id));
    }

    @PatchMapping("/{id}/radius")
public ResponseEntity<ZoneResponse> changeRadius(@PathVariable UUID id, @RequestParam Double radiusKm) {

    ZoneResponse existing = zoneService.getZoneById(id);

    ZoneRequest request = ZoneRequest.builder()
            .name(existing.getName())
            .centerLatitude(existing.getCenterLatitude())
            .centerLongitude(existing.getCenterLongitude())
            .radiusKm(radiusKm)
            .build();

    ZoneResponse updated = zoneService.updateZone(id, request);

    return ResponseEntity.ok(updated);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(@PathVariable UUID id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    // --- Pricing Slab Config Controls ---

    @GetMapping("/{id}/pricing")
    public ResponseEntity<List<PricingSlabResponse>> getPricingSlabs(@PathVariable UUID id) {
        return ResponseEntity.ok(zoneService.getPricingSlabsForZone(id));
    }

    @PostMapping("/pricing")
    public ResponseEntity<PricingSlabResponse> addPricingSlab(@Valid @RequestBody PricingSlabRequest request) {
        return new ResponseEntity<>(zoneService.addPricingSlab(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/pricing/{slabId}")
    public ResponseEntity<Void> deletePricingSlab(@PathVariable UUID slabId) {
        zoneService.deletePricingSlab(slabId);
        return ResponseEntity.noContent().build();
    }
}
