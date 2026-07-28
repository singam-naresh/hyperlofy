package com.hyperlofy.backend.matching.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "matching_candidates")
@SQLDelete(sql = "UPDATE matching_candidates SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingCandidate extends BaseEntity {

    @Column(name = "matching_request_id", nullable = false)
    private UUID matchingRequestId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "eta_minutes", nullable = false)
    private Integer etaMinutes;

    @Column(name = "matching_score", nullable = false)
    private Double matchingScore;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;
}
