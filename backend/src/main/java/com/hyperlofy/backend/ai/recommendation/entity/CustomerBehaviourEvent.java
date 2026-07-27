package com.hyperlofy.backend.ai.recommendation.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "customer_behaviour_events")
@SQLDelete(sql = "UPDATE customer_behaviour_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBehaviourEvent extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // PRODUCT_VIEW, STORE_VIEW, SEARCH, WISHLIST_ADD, CART_ADD, ORDER_PLACED

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "search_query", length = 255)
    private String searchQuery;
}
