package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.merchant.dto.MerchantSettlementDTO;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import com.hyperlofy.backend.merchant.service.MerchantPortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/settlements")
@RequiredArgsConstructor
@Tag(name = "Merchant Settlement Portal API", description = "Endpoints for merchant balance, settlement history, and financial ledger audit")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantSettlementController {

    private final MerchantPortalService merchantPortalService;

    @GetMapping
    @Operation(summary = "Get Settlement Overview", description = "Retrieves complete settlement summary including current balance, lifetime earnings, pending settlements, and completed payouts.")
    public ResponseEntity<MerchantSettlementDTO> getSettlementOverview(@RequestParam UUID merchantId) {
        return ResponseEntity.ok(merchantPortalService.getSettlementOverview(merchantId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get Settlement History", description = "Retrieves completed merchant settlement payout history.")
    public ResponseEntity<List<MerchantLedger>> getSettlementHistory(@RequestParam UUID merchantId) {
        MerchantSettlementDTO overview = merchantPortalService.getSettlementOverview(merchantId);
        return ResponseEntity.ok(overview.getCompletedSettlements());
    }

    @GetMapping("/balance")
    @Operation(summary = "Get Merchant Balance", description = "Retrieves current available balance and lifetime cumulative earnings.")
    public ResponseEntity<MerchantSettlementDTO> getBalance(@RequestParam UUID merchantId) {
        return ResponseEntity.ok(merchantPortalService.getSettlementOverview(merchantId));
    }

    @GetMapping("/ledger")
    @Operation(summary = "Get Merchant Ledger Audit", description = "Retrieves immutable financial ledger entries for merchant product sales and payouts.")
    public ResponseEntity<List<MerchantLedger>> getLedgerHistory(@RequestParam UUID merchantId) {
        MerchantSettlementDTO overview = merchantPortalService.getSettlementOverview(merchantId);
        return ResponseEntity.ok(overview.getLedgerHistory());
    }
}
