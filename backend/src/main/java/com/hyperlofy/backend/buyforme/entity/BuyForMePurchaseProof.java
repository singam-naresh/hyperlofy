package com.hyperlofy.backend.buyforme.entity;

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
@Table(name = "buy_for_me_purchase_proofs")
@SQLDelete(sql = "UPDATE buy_for_me_purchase_proofs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMePurchaseProof extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "store_name", nullable = false, length = 150)
    private String storeName;

    @Column(name = "store_address", columnDefinition = "TEXT")
    private String storeAddress;

    @Column(name = "invoice_number", length = 100)
    private String invoiceNumber;

    @Column(name = "bill_amount", nullable = false)
    private Double billAmount;

    @Builder.Default
    @Column(name = "tax_amount")
    private Double taxAmount = 0.0;

    @Builder.Default
    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Builder.Default
    @Column(name = "purchase_time")
    private ZonedDateTime purchaseTime = ZonedDateTime.now();

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Builder.Default
    @Column(name = "is_approved_by_customer")
    private Boolean isApprovedByCustomer = false;
}
