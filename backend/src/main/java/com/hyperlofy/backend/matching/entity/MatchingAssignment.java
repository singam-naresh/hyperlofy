package com.hyperlofy.backend.matching.entity;

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
@Table(name = "matching_assignments")
@SQLDelete(sql = "UPDATE matching_assignments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingAssignment extends BaseEntity {

    @Column(name = "matching_request_id", nullable = false)
    private UUID matchingRequestId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Builder.Default
    @Column(name = "offer_status", nullable = false, length = 30)
    private String offerStatus = "OFFER_SENT"; // OFFER_SENT, ACCEPTED, REJECTED, TIMED_OUT

    @Builder.Default
    @Column(name = "offer_sent_at")
    private ZonedDateTime offerSentAt = ZonedDateTime.now();

    @Column(name = "responded_at")
    private ZonedDateTime respondedAt;
}
