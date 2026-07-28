package com.hyperlofy.backend.payments.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "payment_tokens")
@SQLDelete(sql = "UPDATE payment_tokens SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentToken extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "provider_name", nullable = false, length = 40)
    private String providerName;

    @Column(name = "payment_token_ref", nullable = false, unique = true)
    private String paymentTokenRef;

    @Column(name = "card_alias", nullable = false, length = 20)
    private String cardAlias;

    @Column(name = "expiry_month", nullable = false)
    private Integer expiryMonth;

    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;

    @Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;
}
