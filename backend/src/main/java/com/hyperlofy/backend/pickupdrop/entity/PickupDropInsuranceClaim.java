package com.hyperlofy.backend.pickupdrop.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "pickup_drop_insurance_claims")
@SQLDelete(sql = "UPDATE pickup_drop_insurance_claims SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropInsuranceClaim extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "claim_type", nullable = false, length = 30)
    private String claimType; // DAMAGED_PARCEL, LOST_PARCEL

    @Column(name = "claimed_amount", nullable = false)
    private Double claimedAmount;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "evidence_url", length = 255)
    private String evidenceUrl;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SUBMITTED";

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;
}
