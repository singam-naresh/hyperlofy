package com.hyperlofy.backend.finance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "finance_cost_centres")
@SQLDelete(sql = "UPDATE finance_cost_centres SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceCostCentre extends BaseEntity {

    @Column(name = "cost_centre_code", nullable = false, unique = true, length = 50)
    private String costCentreCode;

    @Column(name = "cost_centre_name", nullable = false, length = 100)
    private String costCentreName;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "manager_name", length = 100)
    private String managerName;
}
