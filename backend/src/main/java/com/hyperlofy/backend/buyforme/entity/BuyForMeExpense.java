package com.hyperlofy.backend.buyforme.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "buy_for_me_expenses")
@SQLDelete(sql = "UPDATE buy_for_me_expenses SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMeExpense extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "expense_type", nullable = false, length = 50)
    private String expenseType; // PERSONAL_SPEND, ADVANCE_REIMBURSEMENT, PARKING_FEE

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "receipt_url", length = 255)
    private String receiptUrl;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SUBMITTED";

    @Column(name = "approved_by", length = 100)
    private String approvedBy;
}
