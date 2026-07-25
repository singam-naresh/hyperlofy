package com.hyperlofy.backend.catalog.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "catalog_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "sku", nullable = false, length = 120)
    private String sku;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 120)
    private String category;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // createdAt and updatedAt are inherited from BaseEntity
}
