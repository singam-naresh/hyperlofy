package com.hyperlofy.backend.buyforme.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "buy_for_me_substitutions")
@SQLDelete(sql = "UPDATE buy_for_me_substitutions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMeSubstitution extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "original_item_name", nullable = false, length = 150)
    private String originalItemName;

    @Column(name = "substitute_item_name", nullable = false, length = 150)
    private String substituteItemName;

    @Column(name = "substitute_brand", length = 100)
    private String substituteBrand;

    @Column(name = "substitute_price", nullable = false)
    private Double substitutePrice;

    @Column(name = "suggested_by", nullable = false, length = 50)
    private String suggestedBy;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}
