package com.hyperlofy.backend.order.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@SQLDelete(sql = "UPDATE order_items SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @Column(name = "estimated_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(name = "final_price", precision = 12, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "item_status", nullable = false, length = 50)
    private String itemStatus = "AVAILABLE"; // AVAILABLE, OUT_OF_STOCK, REPLACED, CANCELLED
}
