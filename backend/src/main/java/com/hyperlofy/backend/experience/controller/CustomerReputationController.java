package com.hyperlofy.backend.experience.controller;

import com.hyperlofy.backend.experience.entity.CustomerReputation;
import com.hyperlofy.backend.experience.entity.MerchantReputation;
import com.hyperlofy.backend.experience.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reputation")
@RequiredArgsConstructor
@Tag(name = "Customer & Merchant Reputation API", description = "Query customer reviewer ranks, badges (Bronze, Silver, Gold, Elite), trust levels, and merchant CSAT scores")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CustomerReputationController {

    private final CustomerExperienceService experienceService;

    @GetMapping("/customer/{id}")
    @Operation(summary = "Get Customer Reputation & Reviewer Rank", description = "Returns customer reputation score, verified purchase ratio, helpful votes received, and reviewer badge (BRONZE, SILVER, GOLD, ELITE).")
    public ResponseEntity<CustomerReputation> getCustomerReputation(@PathVariable UUID id) {
        return ResponseEntity.ok(experienceService.getCustomerReputation(id));
    }

    @GetMapping("/merchant/{id}")
    @Operation(summary = "Get Merchant Reputation & Trust Metrics", description = "Returns merchant average rating, CSAT percentage, response time, complaint ratio, and AI trust score.")
    public ResponseEntity<MerchantReputation> getMerchantReputation(@PathVariable UUID id) {
        return ResponseEntity.ok(experienceService.getMerchantReputation(id));
    }
}
