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
@RequestMapping("/api/v1/settlements/merchant")
@RequiredArgsConstructor
@Tag(name = "Settlement Engine Merchant API", description = "Endpoints for merchants to track settlement status and historical bank payouts")
@PreAuthorize("hasRole('MERCHANT')")
public class SettlementMerchantController {

    private final SettlementService settlementService;

    @GetMapping("/{merchantId}")
    @Operation(summary = "Get Merchant Settlement History", description = "Returns gross sales, platform commission deductions, taxes, and net payout transfers.")
    public ResponseEntity<List<Settlement>> getMerchantSettlements(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(settlementService.getPayeeSettlements(merchantId));
    }
}
