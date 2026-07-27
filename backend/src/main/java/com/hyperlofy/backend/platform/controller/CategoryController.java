package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.ProductCategory;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Platform Product Category API", description = "Endpoints for category taxonomy, subcategories, images, and sorting")
public class CategoryController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @Operation(summary = "List Product Categories", description = "Retrieves all product categories.")
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        return ResponseEntity.ok(platformService.getAllCategories());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create Category", description = "Creates a new product category.")
    public ResponseEntity<ProductCategory> createCategory(@Valid @RequestBody ProductCategory category) {
        return ResponseEntity.ok(platformService.createCategory(category));
    }
}
