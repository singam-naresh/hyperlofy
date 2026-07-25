package com.hyperlofy.backend.order.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.inventory.dto.InventoryReservationRequest;
import com.hyperlofy.backend.inventory.dto.InventoryReservationResult;
import com.hyperlofy.backend.inventory.service.InventoryReservationService;
import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.order.dto.OrderRequest;
import com.hyperlofy.backend.order.dto.OrderResponse;
import com.hyperlofy.backend.order.dto.OrderStatusUpdateRequest;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderItem;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderItemRepository;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationRequest;
import com.hyperlofy.backend.zone.dto.DeliveryFeeCalculationResponse;
import com.hyperlofy.backend.zone.entity.Zone;
import com.hyperlofy.backend.zone.repository.ZoneRepository;
import com.hyperlofy.backend.zone.service.ZoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final ZoneService zoneService;
    private final InventoryReservationService inventoryReservationService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Verify customer exists and has correct Role
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Customer profile not found", HttpStatus.NOT_FOUND));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new BusinessException("User ID is not standard Customer profile", HttpStatus.BAD_REQUEST);
        }

        // 2. Verify Zone is active
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new BusinessException("Selected zone is invalid", HttpStatus.NOT_FOUND));

        if (!zone.isActive()) {
            throw new BusinessException("Target delivery zone is currently inactive", HttpStatus.BAD_REQUEST);
        }

        // 3. Dynamic Delivery Fee Calculation Engine Interface call
        DeliveryFeeCalculationResponse feeResponse = zoneService.calculateDeliveryFee(
                DeliveryFeeCalculationRequest.builder()
                        .zoneId(zone.getId())
                        .storeLatitude(request.getStoreLatitude())
                        .storeLongitude(request.getStoreLongitude())
                        .deliveryLatitude(request.getDeliveryLatitude())
                        .deliveryLongitude(request.getDeliveryLongitude())
                        .build()
        );

        if (!feeResponse.getWithinZoneBounds()) {
            throw new BusinessException("Delivery location is out of bounds for the selected zone " + zone.getName(), HttpStatus.BAD_REQUEST);
        }

        // 4. Generate highly secure 6-digit Delivery OTP
        String otpCode = String.format("%06d", secureRandom.nextInt(1000000));

        Order order = Order.builder()
                .customer(customer)
                .zone(zone)
                .storeName(request.getStoreName())
                .storeLatitude(request.getStoreLatitude())
                .storeLongitude(request.getStoreLongitude())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryLatitude(request.getDeliveryLatitude())
                .deliveryLongitude(request.getDeliveryLongitude())
                .distanceKm(feeResponse.getDistanceKm())
                .deliveryFee(feeResponse.getDeliveryFee())
                .itemsDesc(request.getItemsDesc())
                .orderStatus(OrderStatus.CREATED)
                .otpCode(otpCode)
                .build();

        Order saved = orderRepository.save(order);

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (OrderItemDto dto : request.getItems()) {
                OrderItem item = OrderItem.builder()
                        .order(saved)
                        .itemName(dto.getItemName())
                        .quantity(dto.getQuantity())
                        .estimatedPrice(dto.getEstimatedPrice())
                        .finalPrice(dto.getFinalPrice())
                        .itemStatus(dto.getItemStatus() != null ? dto.getItemStatus() : "AVAILABLE")
                        .build();

                OrderItem savedItem = orderItemRepository.save(item);

                UUID merchantId = dto.getMerchantId() != null ? dto.getMerchantId() : request.getZoneId();
                if (merchantId != null && (dto.getProductId() != null || dto.getSku() != null) && dto.getQuantity() > 0) {
                    InventoryReservationRequest resReq = InventoryReservationRequest.builder()
                            .reservationId(savedItem.getId())
                            .merchantId(merchantId)
                            .productId(dto.getProductId())
                            .sku(dto.getSku())
                            .quantity(dto.getQuantity())
                            .build();

                    InventoryReservationResult resResult = inventoryReservationService.reserveInventory(resReq);
                    if (!resResult.isSuccess()) {
                        log.warn("Inventory reservation failed for item [{}] in order creation: {}", dto.getItemName(), resResult.getMessage());
                        throw new BusinessException(
                                "Inventory reservation failed for " + dto.getItemName() + ": " + resResult.getMessage(),
                                HttpStatus.CONFLICT
                        );
                    }
                }
            }
        }

        log.info("Successfully created order for Client [{}]: Fee {}", customer.getEmail(), saved.getDeliveryFee());
        return mapToOrderResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + orderId, HttpStatus.NOT_FOUND));

        verifyOrderAccess(order);

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus nextStatus = request.getNextStatus();

        // Strict state machine validation rule checking
        if (!currentStatus.canTransitionTo(nextStatus)) {
            throw new BusinessException(
                    String.format("Strict Constraint Denied: Cannot transition order state from %s to %s", currentStatus, nextStatus),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Verification Logic for ASSIGNED
        if (nextStatus == OrderStatus.ASSIGNED) {
            if (request.getAgentId() == null) {
                throw new BusinessException("Agent ID is required when transitioning to ASSIGNED", HttpStatus.BAD_REQUEST);
            }
            User agent = userRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new BusinessException("Assigned delivery agent not found", HttpStatus.NOT_FOUND));

            if (agent.getRole() != Role.AGENT) {
                throw new BusinessException("Assigned User is not configured as AGENT", HttpStatus.BAD_REQUEST);
            }
            order.setAgent(agent);
            log.info("Agent [ID: {}] assigned to order [{}]", agent.getId(), orderId);
        }

        // Compliance OTP Verification Logic for DELIVERED
        if (nextStatus == OrderStatus.DELIVERED) {
            if (request.getOtpCode() == null || !request.getOtpCode().equals(order.getOtpCode())) {
                throw new BusinessException("Compliance error: Invalid or missing delivery completion OTP validation code", HttpStatus.FORBIDDEN);
            }
            log.info("Delivery confirmation OTP verified successfully for order: {}", orderId);

            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
            for (OrderItem item : items) {
                inventoryReservationService.confirmReservation(item.getId());
            }
        }

        order.setOrderStatus(nextStatus);
        Order updated = orderRepository.save(order);
        log.info("Order status transitioned: {} -> {}", currentStatus, nextStatus);
        return mapToOrderResponse(updated);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + orderId, HttpStatus.NOT_FOUND));

        verifyOrderAccess(order);

        OrderStatus currentStatus = order.getOrderStatus();
        if (!currentStatus.canTransitionTo(OrderStatus.CANCELLED)) {
            throw new BusinessException(
                    "Order cannot be cancelled in status: " + currentStatus,
                    HttpStatus.BAD_REQUEST
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            inventoryReservationService.releaseReservation(item.getId());
        }

        log.info("Order cancelled successfully: {}", orderId);
        return mapToOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found with ID: " + orderId, HttpStatus.NOT_FOUND));
        
        verifyOrderAccess(order);
        
        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrderHistory(UUID customerId) {
        User caller = getCurrentAuthenticatedUser();
        if (caller.getRole() != Role.ADMIN && caller.getRole() != Role.SUPER_ADMIN && !caller.getId().equals(customerId)) {
            throw new BusinessException("Access Denied: You cannot view this customer's history", HttpStatus.FORBIDDEN);
        }
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAgentOrders(UUID agentId) {
        User caller = getCurrentAuthenticatedUser();
        if (caller.getRole() != Role.ADMIN && caller.getRole() != Role.SUPER_ADMIN && !caller.getId().equals(agentId)) {
            throw new BusinessException("Access Denied: You cannot view this agent's orders", HttpStatus.FORBIDDEN);
        }
        return orderRepository.findByAgentIdOrderByCreatedAtDesc(agentId).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private void verifyOrderAccess(Order order) {
        User user = getCurrentAuthenticatedUser();
        
        // If admin, access is always allowed
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        
        // If customer, they must own the order
        if (user.getRole() == Role.CUSTOMER && order.getCustomer().getId().equals(user.getId())) {
            return;
        }
        
        // If agent, they must be the assigned agent of the order
        if (user.getRole() == Role.AGENT && order.getAgent() != null && order.getAgent().getId().equals(user.getId())) {
            return;
        }
        
        throw new BusinessException("Access Denied: You do not own or have access permissions for this order", HttpStatus.FORBIDDEN);
    }

    private User getCurrentAuthenticatedUser() {
        org.springframework.security.core.Authentication auth = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
        
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user profile not found", HttpStatus.UNAUTHORIZED));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByOrderStatusOrderByCreatedAtDesc(status).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // --- Helper Mappers ---

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemDto> itemDtos = null;
        if (order.getId() != null) {
            itemDtos = orderItemRepository.findByOrderId(order.getId()).stream()
                    .map(item -> OrderItemDto.builder()
                            .id(item.getId())
                            .itemName(item.getItemName())
                            .quantity(item.getQuantity())
                            .estimatedPrice(item.getEstimatedPrice())
                            .finalPrice(item.getFinalPrice())
                            .itemStatus(item.getItemStatus())
                            .build())
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .agentId(order.getAgent() != null ? order.getAgent().getId() : null)
                .agentName(order.getAgent() != null ? (order.getAgent().getFirstName() + " " + order.getAgent().getLastName()) : "UNASSIGNED")
                .zoneId(order.getZone().getId())
                .zoneName(order.getZone().getName())
                .storeName(order.getStoreName())
                .storeLatitude(order.getStoreLatitude())
                .storeLongitude(order.getStoreLongitude())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .distanceKm(order.getDistanceKm())
                .deliveryFee(order.getDeliveryFee())
                .itemsDesc(order.getItemsDesc())
                .items(itemDtos)
                .orderStatus(order.getOrderStatus())
                .otpCode(order.getOtpCode())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

