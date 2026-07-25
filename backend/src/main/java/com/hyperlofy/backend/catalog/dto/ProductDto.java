package com.hyperlofy.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID id;
    private UUID merchantId;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String unit;
    private boolean available;
    private int stockQuantity;
    private String imageUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
