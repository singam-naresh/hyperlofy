package com.hyperlofy.backend.ai.orderbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDraftItem {
    private String itemName;
    private String category;
    private int quantity;
    private String unit;
    private String brand;
    private BigDecimal estimatedPrice;
    private boolean substitutionsAllowed;
    private String specialInstructions;
}
