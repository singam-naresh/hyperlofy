package com.hyperlofy.backend.pricing.entity;

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
@Table(name = "pricing_quote_versions")
@SQLDelete(sql = "UPDATE pricing_quote_versions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingQuoteVersion extends BaseEntity {

    @Column(name = "quote_id", nullable = false)
    private UUID quoteId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "recalculation_reason")
    private String recalculationReason;
}
