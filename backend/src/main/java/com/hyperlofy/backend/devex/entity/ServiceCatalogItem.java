package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "service_catalog")
@SQLDelete(sql = "UPDATE service_catalog SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCatalogItem extends BaseEntity {

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "owner_team", nullable = false, length = 100)
    private String ownerTeam;

    @Column(name = "repository_url", nullable = false, length = 255)
    private String repositoryUrl;

    @Builder.Default
    @Column(name = "tech_stack", nullable = false, length = 100)
    private String techStack = "Java 21 / Spring Boot 3";
}
