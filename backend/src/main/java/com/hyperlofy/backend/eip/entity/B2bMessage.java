package com.hyperlofy.backend.eip.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "b2b_messages")
@SQLDelete(sql = "UPDATE b2b_messages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2bMessage extends BaseEntity {

    @Column(name = "partner_id", nullable = false)
    private UUID partnerId;

    @Column(name = "message_type", nullable = false, length = 50)
    private String messageType; // EDI_850_PURCHASE_ORDER, EDI_810_INVOICE, AS2_PAYLOAD

    @Column(name = "control_number", nullable = false, unique = true, length = 100)
    private String controlNumber;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACKNOWLEDGED"; // SENT, RECEIVED, ACKNOWLEDGED, FAILED

    @Builder.Default
    @Column(name = "encryption_type", nullable = false, length = 30)
    private String encryptionType = "AES256_RSA";
}
