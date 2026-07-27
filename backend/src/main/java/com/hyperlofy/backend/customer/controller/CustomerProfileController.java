package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.dto.CustomerProfileDTO;
import com.hyperlofy.backend.customer.dto.LoyaltyRewardsDTO;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Profile & Loyalty API", description = "Endpoints for profile management, preferences, and loyalty rewards tier")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerProfileController {

    private final CustomerExperienceService customerService;

    @GetMapping("/profile")
    @Operation(summary = "Get Customer Profile", description = "Retrieves profile details and preferences for authenticated customer.")
    public ResponseEntity<CustomerProfileDTO> getProfile(Principal principal) {
        return ResponseEntity.ok(customerService.getProfile(UUID.randomUUID()));
    }

    @GetMapping("/loyalty")
    @Operation(summary = "Get Loyalty & Rewards Overview", description = "Retrieves reward points, membership tier, and referral summary.")
    public ResponseEntity<LoyaltyRewardsDTO> getLoyaltyRewards(Principal principal) {
        return ResponseEntity.ok(customerService.getLoyaltyRewards(UUID.randomUUID()));
    }
}
