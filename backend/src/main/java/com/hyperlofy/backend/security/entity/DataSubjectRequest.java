package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "data_subject_requests")
@SQLDelete(sql = "UPDATE data_subject_requests SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSubjectRequest extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType; // RIGHT_TO_ACCESS, RIGHT_TO_ERASURE, RIGHT_TO_RECTIFICATION

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PROCESSED"; // SUBMITTED, IN_REVIEW, PROCESSED, REJECTED

    @Builder.Default
    @Column(name = "processed_at")
    private OffsetDateTime processedAt = OffsetDateTime.now();
}
