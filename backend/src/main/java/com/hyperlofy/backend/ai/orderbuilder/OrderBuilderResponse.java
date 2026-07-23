package com.hyperlofy.backend.ai.orderbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBuilderResponse {
    private boolean success;
    private OrderDraft draft;
    private ValidationResult validationResult;
    private String message;
}
