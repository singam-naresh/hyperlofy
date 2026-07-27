package com.hyperlofy.backend.customer.controller;

import com.hyperlofy.backend.ai.genai.service.AiGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer/ai")
@RequiredArgsConstructor
@Tag(name = "Customer AI Assistant API", description = "Endpoints for RAG-powered customer discovery, order assistance, and interactive support Q&A")
@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPER_ADMIN')")
public class CustomerAiAssistantController {

    private final AiGatewayService aiGatewayService;

    @PostMapping("/query")
    @Operation(summary = "Ask Customer AI Assistant", description = "Queries the RAG-augmented AI assistant for product discovery, order tracking guidance, or platform FAQs.")
    public ResponseEntity<Map<String, Object>> askCustomerAssistant(@RequestParam String query) {
        return ResponseEntity.ok(aiGatewayService.executeRagQuery(query, "CUSTOMER_ASSISTANT"));
    }
}
