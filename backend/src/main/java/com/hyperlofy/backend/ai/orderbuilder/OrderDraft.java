package com.hyperlofy.backend.ai.orderbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDraft {
    private UUID draftId;
    private UUID conversationId;
    private UUID customerId;
    private String plan;
    private String intent;
    private String orderType;
    private String status;
    private List<OrderDraftItem> items = new ArrayList<>();
    private DeliveryDraft deliveryDetails;
    private RecipientDraft recipient;
    private OrderMetadata metadata;
    private ValidationResult validation;
    private Map<String, Object> memoryPreferences = new HashMap<>();
}
