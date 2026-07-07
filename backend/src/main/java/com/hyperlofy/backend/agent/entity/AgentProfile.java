package com.hyperlofy.backend.agent.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "agent_profiles")
@SQLDelete(sql = "UPDATE agent_profiles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @Column(name = "current_gps_latitude")
    private Double currentGpsLatitude;

    @Column(name = "current_gps_longitude")
    private Double currentGpsLongitude;

    @Column(name = "is_available", nullable = false)
    private boolean available = false;

    // Secure Verification Documents
    @Column(name = "pan_number", nullable = false, unique = true, length = 10)
    private String panNumber;

    @Column(name = "pan_doc_url")
    private String panDocUrl;

    @Column(name = "aadhaar_number", nullable = false, unique = true, length = 12)
    private String aadhaarNumber;

    @Column(name = "aadhaar_doc_url")
    private String aadhaarDocUrl;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // Verification workflow tracking
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "suspended_at")
    private OffsetDateTime suspendedAt;

    @Column(name = "suspension_reason")
    private String suspensionReason;
}
