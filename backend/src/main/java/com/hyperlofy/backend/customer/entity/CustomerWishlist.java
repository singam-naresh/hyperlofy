package com.hyperlofy.backend.customer.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "customer_wishlists")
@SQLDelete(sql = "UPDATE customer_wishlists SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWishlist extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Builder.Default
    @Column(name = "folder_name", length = 50)
    private String folderName = "FAVORITES";
}
