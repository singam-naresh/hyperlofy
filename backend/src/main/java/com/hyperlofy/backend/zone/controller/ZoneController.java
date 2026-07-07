package com.hyperlofy.backend.zone.controller;

import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationRequest;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationResponse;
import com.hyperlofy.backend.zone.dto.ZoneResponse;
import com.hyperlofy.backend.zone.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping("/active")
    public ResponseEntity<List<ZoneResponse>> getActiveZones() {
        return ResponseEntity.ok(zoneService.getAllActiveZones());
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<DeliveryFeeCalculationResponse> calculateDeliveryFee(
            @Valid @RequestBody DeliveryFeeCalculationRequest request) {
        return ResponseEntity.ok(zoneService.calculateDeliveryFee(request));
    }
}
