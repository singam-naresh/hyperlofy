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
@Table(name = "customer_reviews")
@SQLDelete(sql = "UPDATE customer_reviews SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_name", length = 150)
    private String userName;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "agent_id")
    private UUID agentId;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1 to 5

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Builder.Default
    @Column(name = "review_type", length = 30)
    private String reviewType = "STORE"; // STORE, PRODUCT, DELIVERY
}
