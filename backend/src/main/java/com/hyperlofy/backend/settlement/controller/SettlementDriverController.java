package com.hyperlofy.backend.settlement.controller;

import com.hyperlofy.backend.settlement.entity.Settlement;
import com.hyperlofy.backend.settlement.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements/driver")
@RequiredArgsConstructor
@Tag(name = "Settlement Engine Driver API", description = "Endpoints for delivery partners to track trip earnings and bank payout statements")
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class SettlementDriverController {

    private final SettlementService settlementService;

    @GetMapping("/{driverId}")
    @Operation(summary = "Get Driver Payout History", description = "Returns trip delivery earnings and processed bank transfers.")
    public ResponseEntity<List<Settlement>> getDriverPayouts(@PathVariable UUID driverId) {
        return ResponseEntity.ok(settlementService.getPayeeSettlements(driverId));
    }
}
