package com.hyperlofy.backend.commerce.builder;

import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.order.dto.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestBuilderResult {

    private OrderRequest orderRequest;
    private MerchantSelectionResponse merchantSelection;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal deliveryFee = BigDecimal.ZERO;
    private BigDecimal estimatedTotal = BigDecimal.ZERO;
    private List<String> warnings = new ArrayList<>();
    private List<Item> availableItems = new ArrayList<>();
    private List<Item> unavailableItems = new ArrayList<>();
    private List<String> missingProducts = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private UUID productId;
        private String sku;
        private String productName;
        private int quantity;
        private java.math.BigDecimal unitPrice;
        private java.math.BigDecimal estimatedPrice;
    }
}
