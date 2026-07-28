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
@Table(name = "matching_requests")
@SQLDelete(sql = "UPDATE matching_requests SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRequest extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "order_type", nullable = false, length = 40)
    private String orderType;

    @Column(name = "pickup_latitude", nullable = false)
    private Double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private Double pickupLongitude;

    @Column(name = "drop_latitude", nullable = false)
    private Double dropLatitude;

    @Column(name = "drop_longitude", nullable = false)
    private Double dropLongitude;

    @Builder.Default
    @Column(name = "priority", length = 20)
    private String priority = "NORMAL";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "MATCH_REQUESTED";

    @Column(name = "assigned_driver_id")
    private UUID assignedDriverId;

    @Builder.Default
    @Column(name = "retry_count")
    private Integer retryCount = 0;
}
