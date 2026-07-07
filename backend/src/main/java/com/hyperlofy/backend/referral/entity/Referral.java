package com.hyperlofy.backend.referral.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "referrals")
@SQLDelete(sql = "UPDATE referrals SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Referral extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_id", nullable = false, unique = true)
    private User referred;

    @Column(name = "reward_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal rewardAmount;

    @Column(name = "is_processed", nullable = false)
    private boolean processed = false;

    @Column(name = "referral_code", length = 30)
    private String referralCode;
}
