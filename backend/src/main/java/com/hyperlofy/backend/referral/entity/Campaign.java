package com.hyperlofy.backend.referral.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "campaigns")
@SQLDelete(sql = "UPDATE campaigns SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 30)
    private String couponCode;

    @Column(name = "reward_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "start_date")
    private OffsetDateTime startDate;

    @Column(name = "end_date")
    private OffsetDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
