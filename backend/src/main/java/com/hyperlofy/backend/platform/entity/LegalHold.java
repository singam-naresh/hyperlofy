package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "legal_holds")
@SQLDelete(sql = "UPDATE legal_holds SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalHold extends BaseEntity {

    @Column(name = "case_id", nullable = false, unique = true, length = 100)
    private String caseId;

    @Column(name = "target_table", nullable = false, length = 100)
    private String targetTable;

    @Column(name = "target_record_id", nullable = false, length = 255)
    private String targetRecordId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "hold_owner", nullable = false, length = 100)
    private String holdOwner;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "effective_date")
    private ZonedDateTime effectiveDate = ZonedDateTime.now();

    @Column(name = "expiration_date")
    private ZonedDateTime expirationDate;
}
