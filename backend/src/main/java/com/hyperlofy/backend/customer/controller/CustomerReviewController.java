package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.entity.CustomerReview;
import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer/reviews")
@RequiredArgsConstructor
@Tag(name = "Customer Ratings & Reviews API", description = "Endpoints for submitting ratings and reviews for stores, products, and delivery partners")
public class CustomerReviewController {

    private final CustomerExperienceService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Submit Rating & Review", description = "Submits a rating and review for store, product, or delivery agent.")
    public ResponseEntity<CustomerReview> submitReview(Principal principal, @Valid @RequestBody CustomerReview review) {
        return ResponseEntity.ok(customerService.submitReview(UUID.randomUUID(), review));
    }
}
