package com.hyperlofy.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private UUID merchantId;
    private String sku;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String unit;
    private boolean available = true;
    private int stockQuantity = 0;
    private String imageUrl;
}
