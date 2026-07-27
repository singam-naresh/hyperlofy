package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.customer.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer/search")
@RequiredArgsConstructor
@Tag(name = "Customer Search Platform API", description = "Endpoints for merchant, product, and category search with distance/rating filters")
public class CustomerSearchController {

    private final CustomerExperienceService customerService;

    @GetMapping
    @Operation(summary = "Search Merchants & Products", description = "Searches system merchants and catalog items with distance and category filters.")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(customerService.search(query, category, maxDistance, page, size));
    }
}
