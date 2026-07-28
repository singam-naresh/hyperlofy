package com.hyperlofy.backend.finance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "finance_credit_notes")
@SQLDelete(sql = "UPDATE finance_credit_notes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceCreditNote extends BaseEntity {

    @Column(name = "credit_note_number", nullable = false, unique = true, length = 100)
    private String creditNoteNumber;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    @Column(name = "refund_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal refundAmount;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ISSUED";
}
