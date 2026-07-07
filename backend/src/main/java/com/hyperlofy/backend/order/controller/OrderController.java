package com.hyperlofy.backend.order.controller;

import com.hyperlofy.backend.order.dto.OrderRequest;
import com.hyperlofy.backend.order.dto.OrderResponse;
import com.hyperlofy.backend.order.dto.OrderStatusUpdateRequest;
import com.hyperlofy.backend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return new ResponseEntity<>(orderService.createOrder(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getCustomerOrderHistory(@PathVariable UUID customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrderHistory(customerId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<OrderResponse>> getAgentOrders(@PathVariable UUID agentId) {
        return ResponseEntity.ok(orderService.getAgentOrders(agentId));
    }
}
