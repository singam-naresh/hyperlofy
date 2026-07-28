package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "partner_applications")
@SQLDelete(sql = "UPDATE partner_applications SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerApplication extends BaseEntity {

    @Column(name = "partner_name", nullable = false, length = 150)
    private String partnerName;

    @Column(name = "app_name", nullable = false, unique = true, length = 150)
    private String appName;

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // PENDING, ACTIVE, SUSPENDED

    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;
}
