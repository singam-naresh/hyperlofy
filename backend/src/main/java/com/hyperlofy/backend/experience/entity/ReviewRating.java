package com.hyperlofy.backend.experience.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "review_ratings")
@SQLDelete(sql = "UPDATE review_ratings SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private CustomerReview review;

    @Builder.Default
    @Column(name = "quality_rating", precision = 3, scale = 2)
    private BigDecimal qualityRating = new BigDecimal("5.00");

    @Builder.Default
    @Column(name = "packaging_rating", precision = 3, scale = 2)
    private BigDecimal packagingRating = new BigDecimal("5.00");

    @Builder.Default
    @Column(name = "delivery_rating", precision = 3, scale = 2)
    private BigDecimal deliveryRating = new BigDecimal("5.00");

    @Builder.Default
    @Column(name = "value_rating", precision = 3, scale = 2)
    private BigDecimal valueRating = new BigDecimal("5.00");

    @Builder.Default
    @Column(name = "communication_rating", precision = 3, scale = 2)
    private BigDecimal communicationRating = new BigDecimal("5.00");
}
