package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-builder")
@RequiredArgsConstructor
public class OrderBuilderController {

    private final OrderBuilderService orderBuilderService;

    @PostMapping("/draft")
    public ResponseEntity<OrderBuilderResponse> buildOrderDraft(@Valid @RequestBody ConversationResponse conversation) {
        return ResponseEntity.ok(orderBuilderService.build(conversation));
    }
}
