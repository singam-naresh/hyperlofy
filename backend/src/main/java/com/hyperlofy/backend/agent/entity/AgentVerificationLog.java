package com.hyperlofy.backend.agent.entity;

import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "agent_verification_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentVerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentProfile agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", nullable = false)
    private User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 30)
    private VerificationStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private VerificationStatus newStatus;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
