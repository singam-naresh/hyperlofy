package com.hyperlofy.backend.ai.orderbuilder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDraftValidator {

    public ValidationResult validate(OrderDraft draft) {
        List<String> errors = new ArrayList<>();

        if (draft == null) {
            errors.add("Draft is required");
            return ValidationResult.builder().valid(false).errors(errors).build();
        }

        if (draft.getOrderType() == null || draft.getOrderType().isBlank()) {
            errors.add("Order type is required");
        }

        if (draft.getItems() == null || draft.getItems().isEmpty()) {
            if (draft.getOrderType() != null && draft.getOrderType().equals("SHOPPING")) {
                errors.add("Shopping drafts must contain at least one item");
            }
        }

        if (draft.getItems() != null) {
            List<String> itemNames = draft.getItems().stream()
                    .map(OrderDraftItem::getItemName)
                    .filter(name -> name != null && !name.isBlank())
                    .collect(Collectors.toList());
            if (itemNames.stream().distinct().count() != itemNames.size()) {
                errors.add("Duplicate items are not allowed");
            }
            for (OrderDraftItem item : draft.getItems()) {
                if (item.getQuantity() <= 0) {
                    errors.add("Invalid quantity");
                }
            }
        }

        if (draft.getOrderType() != null && draft.getOrderType().contains("DELIVERY")) {
            if (draft.getDeliveryDetails() == null || draft.getDeliveryDetails().getPickup() == null || draft.getDeliveryDetails().getPickup().isBlank()) {
                errors.add("Helper drafts must contain pickup address");
            }
            if (draft.getDeliveryDetails() == null || draft.getDeliveryDetails().getDrop() == null || draft.getDeliveryDetails().getDrop().isBlank()) {
                errors.add("Helper drafts must contain drop address");
            }
            if (draft.getRecipient() == null || draft.getRecipient().getPhone() == null || draft.getRecipient().getPhone().isBlank()) {
                errors.add("Helper drafts must contain recipient phone number");
            }
        }

        return ValidationResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .warnings(new ArrayList<>())
                .build();
    }
}
