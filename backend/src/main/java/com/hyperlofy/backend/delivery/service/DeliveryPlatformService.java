package com.hyperlofy.backend.delivery.service;

import com.hyperlofy.backend.agent.entity.AgentPayoutProfile;
import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.agent.repository.AgentPayoutProfileRepository;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.agent.repository.WithdrawalRequestRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.delivery.dto.*;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import com.hyperlofy.backend.ledger.repository.CommissionLedgerRepository;
import com.hyperlofy.backend.ledger.service.LedgerService;
import com.hyperlofy.backend.merchant.dto.SalesTrendDTO;
import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderItem;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderItemRepository;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryPlatformService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AgentRepository agentRepository;
    private final AgentPayoutProfileRepository agentPayoutProfileRepository;
    private final CommissionLedgerRepository commissionLedgerRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;

    // --- MODULE 1: DASHBOARD ---
    @Transactional(readOnly = true)
    public DeliveryDashboardDTO getDashboard(UUID agentId) {
        User agentUser = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Delivery agent user not found: " + agentId, HttpStatus.NOT_FOUND));

        List<Order> assignedOrders = orderRepository.findByAgentIdOrderByCreatedAtDesc(agentUser.getId());
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime startOfWeek = now.minusDays(7);
        OffsetDateTime startOfMonth = now.minusDays(30);

        long todayCount = 0, activeCount = 0, completedCount = 0, cancelledCount = 0, pendingCount = 0;
        double totalDistanceKm = 0.0;

        for (Order o : assignedOrders) {
            OrderStatus s = o.getOrderStatus();
            if (o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfToday)) todayCount++;

            if (s == OrderStatus.ASSIGNED || s == OrderStatus.PICKED_AT_STORE || s == OrderStatus.OUT_FOR_DELIVERY) activeCount++;
            else if (s == OrderStatus.DELIVERED || s == OrderStatus.COMPLETED) {
                completedCount++;
                totalDistanceKm += o.getDistanceKm();
            } else if (s == OrderStatus.CANCELLED || s == OrderStatus.REFUNDED) cancelledCount++;
        }

        // Unassigned pending orders in the system
        List<Order> unassignedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getAgent() == null && (o.getOrderStatus() == OrderStatus.PAYMENT_SUCCESS || o.getOrderStatus() == OrderStatus.CREATED))
                .collect(Collectors.toList());
        pendingCount = unassignedOrders.size();

        // Calculate earnings from CommissionLedger
        List<CommissionLedger> ledgers = commissionLedgerRepository.findByAgentId(agentId);
        BigDecimal todayEarnings = BigDecimal.ZERO;
        BigDecimal weeklyEarnings = BigDecimal.ZERO;
        BigDecimal monthlyEarnings = BigDecimal.ZERO;
        BigDecimal lifetimeEarnings = BigDecimal.ZERO;

        for (CommissionLedger cl : ledgers) {
            BigDecimal share = cl.getAgentShare() != null ? cl.getAgentShare() : BigDecimal.ZERO;
            lifetimeEarnings = lifetimeEarnings.add(share);
            if (cl.getCreatedAt() != null) {
                if (cl.getCreatedAt().isAfter(startOfToday)) todayEarnings = todayEarnings.add(share);
                if (cl.getCreatedAt().isAfter(startOfWeek)) weeklyEarnings = weeklyEarnings.add(share);
                if (cl.getCreatedAt().isAfter(startOfMonth)) monthlyEarnings = monthlyEarnings.add(share);
            }
        }

        AgentPayoutProfile payoutProfile = agentPayoutProfileRepository.findByAgentId(agentId).orElse(null);
        BigDecimal walletBalance = payoutProfile != null ? payoutProfile.getCurrentBalance() : BigDecimal.ZERO;

        AgentProfile profile = agentRepository.findByUserId(agentId).orElse(null);
        BigDecimal rating = profile != null ? profile.getRating() : new BigDecimal("5.00");

        long totalAssigned = assignedOrders.size();
        BigDecimal acceptanceRate = totalAssigned > 0 ? BigDecimal.valueOf(activeCount + completedCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalAssigned), 2, RoundingMode.HALF_UP) : new BigDecimal("100.00");
        BigDecimal completionRate = totalAssigned > 0 ? BigDecimal.valueOf(completedCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalAssigned), 2, RoundingMode.HALF_UP) : new BigDecimal("100.00");

        return DeliveryDashboardDTO.builder()
                .todayDeliveriesCount(todayCount)
                .activeDeliveriesCount(activeCount)
                .completedDeliveriesCount(completedCount)
                .cancelledDeliveriesCount(cancelledCount)
                .pendingAssignmentsCount(pendingCount)
                .todayEarnings(todayEarnings)
                .weeklyEarnings(weeklyEarnings)
                .monthlyEarnings(monthlyEarnings)
                .currentWalletBalance(walletBalance)
                .pendingSettlement(BigDecimal.ZERO)
                .lifetimeEarnings(lifetimeEarnings)
                .averageRating(rating)
                .acceptanceRate(acceptanceRate)
                .completionRate(completionRate)
                .averageDeliveryTimeMinutes(18.5)
                .build();
    }

    // --- MODULE 2: DELIVERY ORDER MANAGEMENT ---
    @Transactional(readOnly = true)
    public Page<DeliveryOrderResponseDTO> getAgentOrders(UUID agentId, int page, int size, OrderStatus statusFilter, String search) {
        User agentUser = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Delivery agent not found: " + agentId, HttpStatus.NOT_FOUND));

        List<Order> assigned = orderRepository.findByAgentIdOrderByCreatedAtDesc(agentUser.getId());

        List<Order> filtered = assigned.stream()
                .filter(o -> statusFilter == null || o.getOrderStatus() == statusFilter)
                .filter(o -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return o.getId().toString().toLowerCase().contains(term) ||
                            (o.getStoreName() != null && o.getStoreName().toLowerCase().contains(term)) ||
                            (o.getDeliveryAddress() != null && o.getDeliveryAddress().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());

        List<DeliveryOrderResponseDTO> content = (start <= end)
                ? filtered.subList(start, end).stream().map(this::mapToDeliveryOrderResponse).collect(Collectors.toList())
                : Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public DeliveryOrderResponseDTO getAgentOrderById(UUID agentId, UUID orderId) {
        Order order = validateOrderAssignment(agentId, orderId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO acceptOrder(UUID agentId, UUID orderId) {
        User agentUser = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Agent user not found: " + agentId, HttpStatus.NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getAgent() != null && !order.getAgent().getId().equals(agentId)) {
            throw new BusinessException("Order already assigned to another delivery agent.", HttpStatus.CONFLICT);
        }

        order.setAgent(agentUser);
        if (order.getOrderStatus() == OrderStatus.PAYMENT_SUCCESS || order.getOrderStatus() == OrderStatus.CREATED) {
            order.setOrderStatus(OrderStatus.ASSIGNED);
        }
        orderRepository.save(order);
        log.info("[Delivery Accept] Order {} accepted by Agent {}", orderId, agentId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO rejectOrder(UUID agentId, UUID orderId, String reason) {
        Order order = validateOrderAssignment(agentId, orderId);
        order.setAgent(null);
        if (order.getOrderStatus() == OrderStatus.ASSIGNED) {
            order.setOrderStatus(OrderStatus.PAYMENT_SUCCESS);
        }
        orderRepository.save(order);
        log.info("[Delivery Reject] Order {} rejected by Agent {}, Reason: {}", orderId, agentId, reason);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO markArrivedMerchant(UUID agentId, UUID orderId) {
        Order order = validateOrderAssignment(agentId, orderId);
        log.info("[Delivery Status] Agent {} arrived at merchant for order {}", agentId, orderId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO markPickedUp(UUID agentId, UUID orderId) {
        Order order = validateOrderAssignment(agentId, orderId);
        order.setOrderStatus(OrderStatus.PICKED_AT_STORE);
        orderRepository.save(order);
        log.info("[Delivery Status] Agent {} picked up order {}", agentId, orderId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO markOutForDelivery(UUID agentId, UUID orderId) {
        Order order = validateOrderAssignment(agentId, orderId);
        order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(order);
        log.info("[Delivery Status] Order {} out for delivery with agent {}", orderId, agentId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO markArrivedCustomer(UUID agentId, UUID orderId) {
        Order order = validateOrderAssignment(agentId, orderId);
        log.info("[Delivery Status] Agent {} arrived at customer location for order {}", agentId, orderId);
        return mapToDeliveryOrderResponse(order);
    }

    @Transactional
    public DeliveryOrderResponseDTO completeOrder(UUID agentId, UUID orderId, String otpCode) {
        Order order = validateOrderAssignment(agentId, orderId);

        if (order.getOtpCode() != null && !order.getOtpCode().isEmpty()) {
            if (otpCode == null || !order.getOtpCode().trim().equals(otpCode.trim())) {
                throw new BusinessException("Invalid OTP verification code provided", HttpStatus.BAD_REQUEST);
            }
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        // Automate Escrow Release & Ledger Booking
        try {
            ledgerService.releaseEscrow(orderId, agentId);
            log.info("[Escrow Released] Order {} delivery completed & escrow released successfully", orderId);
        } catch (Exception e) {
            log.warn("Escrow release on order completion skipped/failed for order {}: {}", orderId, e.getMessage());
        }

        return mapToDeliveryOrderResponse(order);
    }

    // --- MODULE 3: AVAILABILITY & WORK STATUS ---
    @Transactional(readOnly = true)
    public DeliveryStatusDTO getWorkStatus(UUID agentId) {
        AgentProfile profile = agentRepository.findByUserId(agentId)
                .orElseGet(() -> AgentProfile.builder()
                        .workStatus("OFFLINE")
                        .available(false)
                        .currentGpsLatitude(13.6288)
                        .currentGpsLongitude(79.4192)
                        .build());

        return DeliveryStatusDTO.builder()
                .agentId(agentId)
                .workStatus(profile.getWorkStatus())
                .available(profile.isAvailable())
                .currentGpsLatitude(profile.getCurrentGpsLatitude())
                .currentGpsLongitude(profile.getCurrentGpsLongitude())
                .build();
    }

    @Transactional
    public DeliveryStatusDTO updateWorkStatus(UUID agentId, String status) {
        AgentProfile profile = agentRepository.findByUserId(agentId)
                .orElseGet(() -> {
                    User agentUser = userRepository.findById(agentId)
                            .orElseThrow(() -> new BusinessException("User not found: " + agentId, HttpStatus.NOT_FOUND));
                    return AgentProfile.builder()
                            .user(agentUser)
                            .panNumber("ABCDE1234F")
                            .aadhaarNumber("123456789012")
                            .build();
                });

        String normalizedStatus = status.toUpperCase();
        profile.setWorkStatus(normalizedStatus);
        profile.setAvailable("ONLINE".equals(normalizedStatus) || "AVAILABLE".equals(normalizedStatus));
        agentRepository.save(profile);

        log.info("[Agent Work Status Updated] Agent: {}, Status: {}", agentId, normalizedStatus);
        return getWorkStatus(agentId);
    }

    // --- MODULE 4: EARNINGS & SETTLEMENTS ---
    @Transactional(readOnly = true)
    public DeliveryEarningsDTO getEarningsOverview(UUID agentId) {
        List<CommissionLedger> ledgers = commissionLedgerRepository.findByAgentId(agentId);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime startOfWeek = now.minusDays(7);
        OffsetDateTime startOfMonth = now.minusDays(30);

        BigDecimal todayEarnings = BigDecimal.ZERO;
        BigDecimal weeklyEarnings = BigDecimal.ZERO;
        BigDecimal monthlyEarnings = BigDecimal.ZERO;
        BigDecimal lifetimeEarnings = BigDecimal.ZERO;

        for (CommissionLedger cl : ledgers) {
            BigDecimal share = cl.getAgentShare() != null ? cl.getAgentShare() : BigDecimal.ZERO;
            lifetimeEarnings = lifetimeEarnings.add(share);
            if (cl.getCreatedAt() != null) {
                if (cl.getCreatedAt().isAfter(startOfToday)) todayEarnings = todayEarnings.add(share);
                if (cl.getCreatedAt().isAfter(startOfWeek)) weeklyEarnings = weeklyEarnings.add(share);
                if (cl.getCreatedAt().isAfter(startOfMonth)) monthlyEarnings = monthlyEarnings.add(share);
            }
        }

        AgentPayoutProfile payoutProfile = agentPayoutProfileRepository.findByAgentId(agentId).orElse(null);
        BigDecimal currentBalance = payoutProfile != null ? payoutProfile.getCurrentBalance() : BigDecimal.ZERO;

        List<WithdrawalRequest> payouts = withdrawalRequestRepository.findByAgentIdOrderByCreatedAtDesc(agentId);

        return DeliveryEarningsDTO.builder()
                .todayEarnings(todayEarnings)
                .weeklyEarnings(weeklyEarnings)
                .monthlyEarnings(monthlyEarnings)
                .lifetimeEarnings(lifetimeEarnings)
                .currentBalance(currentBalance)
                .pendingSettlementAmount(BigDecimal.ZERO)
                .ledgerHistory(ledgers)
                .payoutHistory(payouts)
                .build();
    }

    // --- MODULE 5: PERFORMANCE ANALYTICS ---
    @Transactional(readOnly = true)
    public DeliveryAnalyticsDTO getAnalytics(UUID agentId) {
        User agentUser = userRepository.findById(agentId).orElse(null);
        List<Order> orders = agentUser != null ? orderRepository.findByAgentIdOrderByCreatedAtDesc(agentUser.getId()) : Collections.emptyList();

        long completed = 0, cancelled = 0;
        double totalDistance = 0.0;

        for (Order o : orders) {
            OrderStatus s = o.getOrderStatus();
            if (s == OrderStatus.DELIVERED || s == OrderStatus.COMPLETED) {
                completed++;
                totalDistance += o.getDistanceKm();
            } else if (s == OrderStatus.CANCELLED || s == OrderStatus.REFUNDED) cancelled++;
        }

        long total = orders.size();
        BigDecimal acceptanceRate = total > 0 ? BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) : new BigDecimal("100.00");
        BigDecimal completionRate = total > 0 ? BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) : new BigDecimal("100.00");

        AgentProfile profile = agentRepository.findByUserId(agentId).orElse(null);
        BigDecimal rating = profile != null ? profile.getRating() : new BigDecimal("5.00");

        List<SalesTrendDTO> revenueTrend = getEarningsTrends(agentId);

        return DeliveryAnalyticsDTO.builder()
                .acceptanceRate(acceptanceRate)
                .completionRate(completionRate)
                .averageDeliveryTimeMinutes(18.5)
                .customerRating(rating)
                .lateDeliveriesCount(0L)
                .cancelledDeliveriesCount(cancelled)
                .completedDeliveriesCount(completed)
                .totalDistanceKm(totalDistance)
                .revenueTrend(revenueTrend)
                .dailyPerformance(new BigDecimal("98.50"))
                .weeklyPerformance(new BigDecimal("97.20"))
                .monthlyPerformance(new BigDecimal("99.00"))
                .build();
    }

    // --- MODULE 6: PROFILE ---
    @Transactional(readOnly = true)
    public DeliveryProfileDTO getProfile(UUID agentId) {
        User user = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Agent user not found: " + agentId, HttpStatus.NOT_FOUND));

        AgentProfile profile = agentRepository.findByUserId(agentId)
                .orElseGet(() -> AgentProfile.builder()
                        .user(user)
                        .vehicleType("BIKE")
                        .vehicleNumber("AP 03 AB 1234")
                        .drivingLicence("DL-1420110012345")
                        .emergencyContact("+919876543211")
                        .rating(new BigDecimal("5.00"))
                        .build());

        AgentPayoutProfile payoutProfile = agentPayoutProfileRepository.findByAgentId(agentId).orElse(null);

        return DeliveryProfileDTO.builder()
                .agentId(agentId)
                .fullName(user.getFirstName() + " " + user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profilePhoto(profile.getProfileImageUrl())
                .vehicleType(profile.getVehicleType())
                .vehicleNumber(profile.getVehicleNumber())
                .drivingLicence(profile.getDrivingLicence())
                .emergencyContact(profile.getEmergencyContact())
                .bankHolderName(payoutProfile != null ? payoutProfile.getBankHolderName() : "N/A")
                .bankAccountNumber(payoutProfile != null ? payoutProfile.getBankAccountNumber() : "N/A")
                .bankIfscCode(payoutProfile != null ? payoutProfile.getBankIfscCode() : "N/A")
                .rating(profile.getRating())
                .build();
    }

    @Transactional
    public DeliveryProfileDTO updateProfile(UUID agentId, DeliveryProfileDTO dto) {
        User user = userRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException("Agent user not found: " + agentId, HttpStatus.NOT_FOUND));

        if (dto.getFullName() != null) {
            String[] parts = dto.getFullName().trim().split("\\s+", 2);
            user.setFirstName(parts[0]);
            if (parts.length > 1) user.setLastName(parts[1]);
        }
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        userRepository.save(user);

        AgentProfile profile = agentRepository.findByUserId(agentId)
                .orElseGet(() -> AgentProfile.builder()
                        .user(user)
                        .panNumber("ABCDE1234F")
                        .aadhaarNumber("123456789012")
                        .build());

        if (dto.getVehicleType() != null) profile.setVehicleType(dto.getVehicleType());
        if (dto.getVehicleNumber() != null) profile.setVehicleNumber(dto.getVehicleNumber());
        if (dto.getDrivingLicence() != null) profile.setDrivingLicence(dto.getDrivingLicence());
        if (dto.getEmergencyContact() != null) profile.setEmergencyContact(dto.getEmergencyContact());
        if (dto.getProfilePhoto() != null) profile.setProfileImageUrl(dto.getProfilePhoto());

        agentRepository.save(profile);

        if (dto.getBankHolderName() != null || dto.getBankAccountNumber() != null || dto.getBankIfscCode() != null) {
            AgentPayoutProfile payout = agentPayoutProfileRepository.findByAgentId(agentId)
                    .orElseGet(() -> AgentPayoutProfile.builder().agentId(agentId).build());

            if (dto.getBankHolderName() != null) payout.setBankHolderName(dto.getBankHolderName());
            if (dto.getBankAccountNumber() != null) payout.setBankAccountNumber(dto.getBankAccountNumber());
            if (dto.getBankIfscCode() != null) payout.setBankIfscCode(dto.getBankIfscCode());

            agentPayoutProfileRepository.save(payout);
        }

        return getProfile(agentId);
    }

    // --- HELPER METHODS ---
    private Order validateOrderAssignment(UUID agentId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getAgent() != null && !order.getAgent().getId().equals(agentId)) {
            throw new BusinessException("Unauthorized access: Order is assigned to another delivery agent.", HttpStatus.FORBIDDEN);
        }
        return order;
    }

    private DeliveryOrderResponseDTO mapToDeliveryOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemDto> itemDtos = items.stream().map(i -> OrderItemDto.builder()
                .id(i.getId())
                .itemName(i.getItemName())
                .productName(i.getItemName())
                .quantity(i.getQuantity())
                .estimatedPrice(i.getEstimatedPrice())
                .finalPrice(i.getFinalPrice())
                .subtotal((i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                .build()).collect(Collectors.toList());

        return DeliveryOrderResponseDTO.builder()
                .orderId(order.getId())
                .storeName(order.getStoreName())
                .storeAddress("Store Location, " + order.getStoreName())
                .storeLatitude(order.getStoreLatitude())
                .storeLongitude(order.getStoreLongitude())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryLatitude(order.getDeliveryLatitude())
                .deliveryLongitude(order.getDeliveryLongitude())
                .distanceKm(order.getDistanceKm())
                .deliveryFee(order.getDeliveryFee())
                .status(order.getOrderStatus())
                .otpCode(order.getOtpCode())
                .items(itemDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private List<SalesTrendDTO> getEarningsTrends(UUID agentId) {
        List<CommissionLedger> ledgers = commissionLedgerRepository.findByAgentId(agentId);
        Map<String, SalesTrendDTO> trendMap = new TreeMap<>();
        for (CommissionLedger cl : ledgers) {
            if (cl.getCreatedAt() != null) {
                String dateKey = cl.getCreatedAt().toLocalDate().toString();
                BigDecimal share = cl.getAgentShare() != null ? cl.getAgentShare() : BigDecimal.ZERO;

                SalesTrendDTO trend = trendMap.getOrDefault(dateKey, new SalesTrendDTO(dateKey, BigDecimal.ZERO, 0L));
                trend.setRevenue(trend.getRevenue().add(share));
                trend.setOrderCount(trend.getOrderCount() + 1);
                trendMap.put(dateKey, trend);
            }
        }
        return new ArrayList<>(trendMap.values());
    }
}
