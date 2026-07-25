package com.hyperlofy.backend.catalog.controller;

import com.hyperlofy.backend.catalog.dto.CreateProductRequest;
import com.hyperlofy.backend.catalog.dto.ProductDto;
import com.hyperlofy.backend.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable UUID id) {
        Optional<ProductDto> p = productService.findById(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ProductDto>> getByMerchant(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(productService.findByMerchantId(merchantId));
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody CreateProductRequest req) {
        ProductDto dto = productService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable UUID id, @Valid @RequestBody CreateProductRequest req) {
        Optional<ProductDto> updated = productService.update(id, req);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
