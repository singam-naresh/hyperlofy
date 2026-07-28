package com.hyperlofy.backend.engagement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "marketing_campaigns")
@SQLDelete(sql = "UPDATE marketing_campaigns SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingCampaign extends BaseEntity {

    @Column(name = "campaign_code", nullable = false, unique = true, length = 100)
    private String campaignCode;

    @Column(name = "campaign_name", nullable = false, length = 150)
    private String campaignName;

    @Builder.Default
    @Column(name = "campaign_type", nullable = false, length = 80)
    private String campaignType = "FESTIVAL_SALE"; // FESTIVAL_SALE, WINBACK, WELCOME, BIRTHDAY, FLASH_SALE

    @Builder.Default
    @Column(name = "target_segment", nullable = false, length = 80)
    private String targetSegment = "VIP_CUSTOMER";

    @Column(name = "discount_coupon_code", length = 50)
    private String discountCouponCode;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // DRAFT, ACTIVE, COMPLETED, PAUSED

    @Builder.Default
    @Column(name = "total_recipients", nullable = false)
    private Integer totalRecipients = 50000;

    @Builder.Default
    @Column(name = "converted_count", nullable = false)
    private Integer convertedCount = 4250;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
