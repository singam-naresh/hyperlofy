package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.dto.CustomerHomeDTO;
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
@RequestMapping("/api/v1/customer/home")
@RequiredArgsConstructor
@Tag(name = "Customer Home Experience API", description = "Endpoints for unified home experience feed, banners, categories, nearby stores, and offers")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerHomeController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Get Customer Home Feed", description = "Retrieves aggregated home feed including marketing banners, taxonomy categories, nearby merchants, and active discount coupons.")
    public ResponseEntity<CustomerHomeDTO> getHomeExperience(Principal principal) {
        return ResponseEntity.ok(customerService.getHomeExperience(UUID.randomUUID()));
    }
}
