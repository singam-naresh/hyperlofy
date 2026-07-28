package com.hyperlofy.backend.pickupdrop.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "pickup_drop_otps")
@SQLDelete(sql = "UPDATE pickup_drop_otps SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropOtp extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "otp_code", nullable = false, length = 10)
    private String otpCode;

    @Column(name = "otp_type", nullable = false, length = 20)
    private String otpType; // PICKUP, DELIVERY

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;
}
