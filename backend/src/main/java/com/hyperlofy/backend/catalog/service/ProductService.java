package com.hyperlofy.backend.catalog.service;

import com.hyperlofy.backend.catalog.dto.CreateProductRequest;
import com.hyperlofy.backend.catalog.dto.ProductDto;
import com.hyperlofy.backend.catalog.entity.Product;
import com.hyperlofy.backend.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(UUID id) {
        return productRepository.findById(id).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> findByMerchantId(UUID merchantId) {
        return productRepository.findByMerchantId(merchantId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ProductDto create(CreateProductRequest req) {
        Product p = Product.builder()
                .merchantId(req.getMerchantId())
                .sku(req.getSku())
                .name(req.getName())
                .description(req.getDescription())
                .category(req.getCategory())
                .price(req.getPrice())
                .unit(req.getUnit())
                .available(req.isAvailable())
                .stockQuantity(req.getStockQuantity())
                .imageUrl(req.getImageUrl())
                .build();

        Product saved = productRepository.save(p);
        return toDto(saved);
    }

    @Transactional
    public Optional<ProductDto> update(UUID id, CreateProductRequest req) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) return Optional.empty();
        Product p = existing.get();
        p.setMerchantId(req.getMerchantId());
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setCategory(req.getCategory());
        p.setPrice(req.getPrice());
        p.setUnit(req.getUnit());
        p.setAvailable(req.isAvailable());
        p.setStockQuantity(req.getStockQuantity());
        p.setImageUrl(req.getImageUrl());
        Product saved = productRepository.save(p);
        return Optional.of(toDto(saved));
    }

    @Transactional
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product p) {
        if (p == null) return null;
        return ProductDto.builder()
                .id(p.getId())
                .merchantId(p.getMerchantId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .category(p.getCategory())
                .price(p.getPrice())
                .unit(p.getUnit())
                .available(p.isAvailable())
                .stockQuantity(p.getStockQuantity())
                .imageUrl(p.getImageUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
