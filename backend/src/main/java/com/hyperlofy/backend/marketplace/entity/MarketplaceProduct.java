package com.hyperlofy.backend.marketplace.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "marketplace_products")
@SQLDelete(sql = "UPDATE marketplace_products SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceProduct extends BaseEntity {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "brand_id")
    private UUID brandId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;

    @Column(name = "barcode", unique = true, length = 100)
    private String barcode;

    @Column(name = "hsn_code", length = 50)
    private String hsnCode;

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;

    @Builder.Default
    @Column(name = "is_veg")
    private Boolean isVeg = true;

    @Builder.Default
    @Column(name = "product_status", nullable = false, length = 30)
    private String productStatus = "ACTIVE"; // ACTIVE, INACTIVE, HIDDEN, OUT_OF_STOCK
}
