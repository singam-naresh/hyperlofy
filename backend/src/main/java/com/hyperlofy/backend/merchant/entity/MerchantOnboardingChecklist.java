package com.hyperlofy.backend.merchant.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "merchant_onboarding_checklists")
@SQLDelete(sql = "UPDATE merchant_onboarding_checklists SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantOnboardingChecklist extends BaseEntity {

    @Column(name = "merchant_id", nullable = false, unique = true)
    private UUID merchantId;

    @Builder.Default
    @Column(name = "kyc_completed")
    private Boolean kycCompleted = false;

    @Builder.Default
    @Column(name = "bank_verified")
    private Boolean bankVerified = false;

    @Builder.Default
    @Column(name = "store_created")
    private Boolean storeCreated = false;

    @Builder.Default
    @Column(name = "documents_uploaded")
    private Boolean documentsUploaded = false;

    @Builder.Default
    @Column(name = "admin_approved")
    private Boolean adminApproved = false;

    @Builder.Default
    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;
}
