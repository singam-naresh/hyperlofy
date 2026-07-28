package com.hyperlofy.backend.marketplace.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@SQLDelete(sql = "UPDATE product_variants SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_name", nullable = false, length = 100)
    private String variantName; // 250g, 500g, 1kg, 1L, Pack of 2

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "barcode", unique = true, length = 100)
    private String barcode;

    @Column(name = "mrp", nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "offer_price", precision = 12, scale = 2)
    private BigDecimal offerPrice;

    @Builder.Default
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;

    @Builder.Default
    @Column(name = "weight_unit", nullable = false, length = 20)
    private String weightUnit = "g";

    @Builder.Default
    @Column(name = "variant_weight", nullable = false)
    private Double variantWeight = 1.0;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
