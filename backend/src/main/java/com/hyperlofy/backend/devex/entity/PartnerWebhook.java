package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "partner_webhooks")
@SQLDelete(sql = "UPDATE partner_webhooks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerWebhook extends BaseEntity {

    @Column(name = "partner_app_id", nullable = false)
    private UUID partnerAppId;

    @Column(name = "target_url", nullable = false, length = 255)
    private String targetUrl;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "secret_key", nullable = false, length = 100)
    private String secretKey;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
