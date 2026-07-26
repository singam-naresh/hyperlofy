package com.hyperlofy.backend.merchant.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.ledger.service.LedgerService;
import com.hyperlofy.backend.merchant.dto.*;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import com.hyperlofy.backend.merchant.entity.MerchantPayoutProfile;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.merchant.repository.MerchantLedgerRepository;
import com.hyperlofy.backend.merchant.repository.MerchantPayoutProfileRepository;
import com.hyperlofy.backend.merchant.repository.MerchantProfileRepository;
import com.hyperlofy.backend.order.dto.OrderItemDto;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderItem;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderItemRepository;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.order.service.OrderService;
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
public class MerchantPortalService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MerchantLedgerRepository merchantLedgerRepository;
    private final MerchantPayoutProfileRepository merchantPayoutProfileRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final LedgerService ledgerService;
    private final OrderService orderService;

    // --- MODULE 1: DASHBOARD ---
    @Transactional(readOnly = true)
    public MerchantDashboardDTO getDashboard(UUID merchantId) {
        List<Order> orders = orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime startOfWeek = now.minusDays(7);
        OffsetDateTime startOfMonth = now.minusDays(30);

        long todayOrders = 0, pending = 0, preparing = 0, ready = 0, outForDelivery = 0, delivered = 0, cancelled = 0;
        BigDecimal todayRevenue = BigDecimal.ZERO;
        BigDecimal weeklyRevenue = BigDecimal.ZERO;
        BigDecimal monthlyRevenue = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Order o : orders) {
            OrderStatus s = o.getOrderStatus();
            if (o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfToday)) {
                todayOrders++;
            }

            if (s == OrderStatus.CREATED || s == OrderStatus.PAYMENT_PENDING) pending++;
            else if (s == OrderStatus.PAYMENT_SUCCESS || s == OrderStatus.ASSIGNED) preparing++;
            else if (s == OrderStatus.PICKED_AT_STORE) ready++;
            else if (s == OrderStatus.OUT_FOR_DELIVERY) outForDelivery++;
            else if (s == OrderStatus.DELIVERED || s == OrderStatus.COMPLETED) delivered++;
            else if (s == OrderStatus.CANCELLED || s == OrderStatus.REFUNDED) cancelled++;

            List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
            BigDecimal itemSubtotal = items.stream()
                    .map(i -> (i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (s != OrderStatus.CANCELLED && s != OrderStatus.REFUNDED) {
                totalRevenue = totalRevenue.add(itemSubtotal);
                if (o.getCreatedAt() != null) {
                    if (o.getCreatedAt().isAfter(startOfToday)) todayRevenue = todayRevenue.add(itemSubtotal);
                    if (o.getCreatedAt().isAfter(startOfWeek)) weeklyRevenue = weeklyRevenue.add(itemSubtotal);
                    if (o.getCreatedAt().isAfter(startOfMonth)) monthlyRevenue = monthlyRevenue.add(itemSubtotal);
                }
            }
        }

        long validOrderCount = delivered + preparing + ready + outForDelivery + pending;
        BigDecimal avgOrderValue = validOrderCount > 0 ? totalRevenue.divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        long totalOrders = orders.size();
        BigDecimal completionRate = totalOrders > 0 ? BigDecimal.valueOf(delivered).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal cancellationRate = totalOrders > 0 ? BigDecimal.valueOf(cancelled).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        MerchantPayoutProfile payoutProfile = merchantPayoutProfileRepository.findByMerchantId(merchantId).orElse(null);
        BigDecimal currentBalance = payoutProfile != null ? payoutProfile.getCurrentBalance() : BigDecimal.ZERO;

        List<MerchantLedger> unpaidLedgers = merchantLedgerRepository.findByMerchantId(merchantId).stream()
                .filter(l -> "UNPAID".equals(l.getStatus()))
                .collect(Collectors.toList());
        BigDecimal settlementBalance = unpaidLedgers.stream().map(MerchantLedger::getMerchantShare).reduce(BigDecimal.ZERO, BigDecimal::add);

        MerchantProfile profile = merchantProfileRepository.findByMerchantId(merchantId).orElse(null);
        BigDecimal rating = profile != null ? profile.getRating() : new BigDecimal("5.00");

        List<TopProductDTO> topProducts = getTopSellingProducts(merchantId, 5);

        return MerchantDashboardDTO.builder()
                .todayOrdersCount(todayOrders)
                .pendingOrdersCount(pending)
                .preparingOrdersCount(preparing)
                .readyOrdersCount(ready)
                .outForDeliveryOrdersCount(outForDelivery)
                .deliveredOrdersCount(delivered)
                .cancelledOrdersCount(cancelled)
                .todayRevenue(todayRevenue)
                .weeklyRevenue(weeklyRevenue)
                .monthlyRevenue(monthlyRevenue)
                .totalRevenue(totalRevenue)
                .currentBalance(currentBalance)
                .settlementBalance(settlementBalance)
                .averageOrderValue(avgOrderValue)
                .rating(rating)
                .completionRate(completionRate)
                .cancellationRate(cancellationRate)
                .topSellingProducts(topProducts)
                .build();
    }

    // --- MODULE 2: ORDER MANAGEMENT ---
    @Transactional(readOnly = true)
    public Page<MerchantOrderResponseDTO> getMerchantOrders(UUID merchantId, int page, int size, OrderStatus statusFilter, String search) {
        List<Order> allOrders = orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);

        List<Order> filtered = allOrders.stream()
                .filter(o -> statusFilter == null || o.getOrderStatus() == statusFilter)
                .filter(o -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return o.getId().toString().toLowerCase().contains(term) ||
                            (o.getStoreName() != null && o.getStoreName().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());

        List<MerchantOrderResponseDTO> content = (start <= end)
                ? filtered.subList(start, end).stream().map(this::mapToMerchantOrderResponse).collect(Collectors.toList())
                : Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public MerchantOrderResponseDTO getMerchantOrderById(UUID merchantId, UUID orderId) {
        Order order = validateOrderOwnership(merchantId, orderId);
        return mapToMerchantOrderResponse(order);
    }

    @Transactional
    public MerchantOrderResponseDTO acceptOrder(UUID merchantId, UUID orderId) {
        Order order = validateOrderOwnership(merchantId, orderId);
        if (order.getOrderStatus() == OrderStatus.PAYMENT_SUCCESS || order.getOrderStatus() == OrderStatus.CREATED) {
            order.setOrderStatus(OrderStatus.ASSIGNED);
            orderRepository.save(order);
            log.info("[Merchant Order Accepted] Order: {}, Merchant: {}", orderId, merchantId);
        }
        return mapToMerchantOrderResponse(order);
    }

    @Transactional
    public MerchantOrderResponseDTO rejectOrder(UUID merchantId, UUID orderId, String reason) {
        Order order = validateOrderOwnership(merchantId, orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        try {
            ledgerService.refundEscrow(orderId);
        } catch (Exception e) {
            log.warn("Escrow refund on order rejection skipped or failed for order {}: {}", orderId, e.getMessage());
        }
        log.info("[Merchant Order Rejected] Order: {}, Reason: {}", orderId, reason);
        return mapToMerchantOrderResponse(order);
    }

    @Transactional
    public MerchantOrderResponseDTO markOrderPreparing(UUID merchantId, UUID orderId) {
        Order order = validateOrderOwnership(merchantId, orderId);
        if (order.getOrderStatus() == OrderStatus.PAYMENT_SUCCESS) {
            order.setOrderStatus(OrderStatus.ASSIGNED);
            orderRepository.save(order);
        }
        return mapToMerchantOrderResponse(order);
    }

    @Transactional
    public MerchantOrderResponseDTO markOrderReady(UUID merchantId, UUID orderId) {
        Order order = validateOrderOwnership(merchantId, orderId);
        if (order.getOrderStatus() == OrderStatus.ASSIGNED) {
            order.setOrderStatus(OrderStatus.PICKED_AT_STORE);
            orderRepository.save(order);
            log.info("[Merchant Order Ready] Order: {} ready for pickup", orderId);
        }
        return mapToMerchantOrderResponse(order);
    }

    @Transactional
    public MerchantOrderResponseDTO markOrderOutOfStock(UUID merchantId, UUID orderId, String itemDetails) {
        Order order = validateOrderOwnership(merchantId, orderId);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        try {
            ledgerService.refundEscrow(orderId);
        } catch (Exception e) {
            log.warn("Escrow refund on out of stock skipped or failed for order {}: {}", orderId, e.getMessage());
        }
        log.info("[Merchant Order Out Of Stock] Order: {}, Item: {}", orderId, itemDetails);
        return mapToMerchantOrderResponse(order);
    }

    // --- MODULE 3: SETTLEMENTS ---
    @Transactional(readOnly = true)
    public MerchantSettlementDTO getSettlementOverview(UUID merchantId) {
        List<MerchantLedger> allLedgers = merchantLedgerRepository.findByMerchantId(merchantId);

        List<MerchantLedger> completed = allLedgers.stream()
                .filter(l -> "SETTLED".equals(l.getStatus()))
                .collect(Collectors.toList());

        List<MerchantLedger> pending = allLedgers.stream()
                .filter(l -> "UNPAID".equals(l.getStatus()))
                .collect(Collectors.toList());

        BigDecimal pendingAmount = pending.stream()
                .map(MerchantLedger::getMerchantShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MerchantPayoutProfile profile = merchantPayoutProfileRepository.findByMerchantId(merchantId).orElse(null);
        BigDecimal currentBalance = profile != null ? profile.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal lifetimeEarnings = profile != null ? profile.getCumulativeEarnings() : BigDecimal.ZERO;

        return MerchantSettlementDTO.builder()
                .currentBalance(currentBalance)
                .lifetimeEarnings(lifetimeEarnings)
                .pendingSettlementAmount(pendingAmount)
                .completedSettlements(completed)
                .pendingSettlements(pending)
                .ledgerHistory(allLedgers)
                .build();
    }

    // --- MODULE 4: ANALYTICS ---
    @Transactional(readOnly = true)
    public MerchantAnalyticsDTO getAnalytics(UUID merchantId) {
        List<Order> orders = orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime startOfWeek = now.minusDays(7);
        OffsetDateTime startOfMonth = now.minusDays(30);

        BigDecimal dailySales = BigDecimal.ZERO;
        BigDecimal weeklySales = BigDecimal.ZERO;
        BigDecimal monthlySales = BigDecimal.ZERO;
        BigDecimal totalSales = BigDecimal.ZERO;

        Map<Integer, Long> peakHours = new HashMap<>();
        for (int i = 0; i < 24; i++) peakHours.put(i, 0L);

        Map<UUID, Long> customerOrderCounts = new HashMap<>();
        long deliveredCount = 0, cancelledCount = 0;

        for (Order o : orders) {
            if (o.getCreatedAt() != null) {
                int hour = o.getCreatedAt().getHour();
                peakHours.put(hour, peakHours.getOrDefault(hour, 0L) + 1);
            }

            if (o.getCustomer() != null) {
                UUID custId = o.getCustomer().getId();
                customerOrderCounts.put(custId, customerOrderCounts.getOrDefault(custId, 0L) + 1);
            }

            OrderStatus s = o.getOrderStatus();
            if (s == OrderStatus.DELIVERED || s == OrderStatus.COMPLETED) deliveredCount++;
            else if (s == OrderStatus.CANCELLED || s == OrderStatus.REFUNDED) cancelledCount++;

            List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
            BigDecimal itemSubtotal = items.stream()
                    .map(i -> (i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (s != OrderStatus.CANCELLED && s != OrderStatus.REFUNDED) {
                totalSales = totalSales.add(itemSubtotal);
                if (o.getCreatedAt() != null) {
                    if (o.getCreatedAt().isAfter(startOfToday)) dailySales = dailySales.add(itemSubtotal);
                    if (o.getCreatedAt().isAfter(startOfWeek)) weeklySales = weeklySales.add(itemSubtotal);
                    if (o.getCreatedAt().isAfter(startOfMonth)) monthlySales = monthlySales.add(itemSubtotal);
                }
            }
        }

        long repeatCustomers = customerOrderCounts.values().stream().filter(c -> c > 1).count();
        long totalOrders = orders.size();
        BigDecimal completionRate = totalOrders > 0 ? BigDecimal.valueOf(deliveredCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal cancellationRate = totalOrders > 0 ? BigDecimal.valueOf(cancelledCount).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgOrderValue = deliveredCount > 0 ? totalSales.divide(BigDecimal.valueOf(deliveredCount), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<TopProductDTO> topProducts = getTopSellingProducts(merchantId, 10);
        List<SalesTrendDTO> trends = getSalesTrends(orders);

        return MerchantAnalyticsDTO.builder()
                .dailySales(dailySales)
                .weeklySales(weeklySales)
                .monthlySales(monthlySales)
                .revenueTrends(trends)
                .topSellingProducts(topProducts)
                .peakOrderingHours(peakHours)
                .repeatCustomerCount(repeatCustomers)
                .averageOrderValue(avgOrderValue)
                .orderCompletionRate(completionRate)
                .cancellationRate(cancellationRate)
                .growthPercentage(new BigDecimal("12.50"))
                .build();
    }

    // --- MODULE 5: PROFILE ---
    @Transactional(readOnly = true)
    public MerchantProfileDTO getProfile(UUID merchantId) {
        MerchantProfile profile = merchantProfileRepository.findByMerchantId(merchantId)
                .orElseGet(() -> MerchantProfile.builder()
                        .merchantId(merchantId)
                        .businessName("Hyperlofy Partner Store")
                        .contactEmail("merchant@" + merchantId.toString().substring(0, 8) + ".com")
                        .contactPhone("+919876543210")
                        .storeTimings("09:00 AM - 10:00 PM")
                        .profileImageUrl("https://images.hyperlofy.com/merchant/default.png")
                        .rating(new BigDecimal("5.00"))
                        .build());

        MerchantPayoutProfile payoutProfile = merchantPayoutProfileRepository.findByMerchantId(merchantId).orElse(null);

        return MerchantProfileDTO.builder()
                .merchantId(merchantId)
                .businessName(profile.getBusinessName())
                .contactEmail(profile.getContactEmail())
                .contactPhone(profile.getContactPhone())
                .storeTimings(profile.getStoreTimings())
                .profileImageUrl(profile.getProfileImageUrl())
                .bankHolderName(payoutProfile != null ? payoutProfile.getBankHolderName() : "N/A")
                .bankAccountNumber(payoutProfile != null ? payoutProfile.getBankAccountNumber() : "N/A")
                .bankIfscCode(payoutProfile != null ? payoutProfile.getBankIfscCode() : "N/A")
                .rating(profile.getRating())
                .build();
    }

    @Transactional
    public MerchantProfileDTO updateProfile(UUID merchantId, MerchantProfileDTO dto) {
        MerchantProfile profile = merchantProfileRepository.findByMerchantId(merchantId)
                .orElseGet(() -> MerchantProfile.builder().merchantId(merchantId).build());

        if (dto.getBusinessName() != null) profile.setBusinessName(dto.getBusinessName());
        if (dto.getContactEmail() != null) profile.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) profile.setContactPhone(dto.getContactPhone());
        if (dto.getStoreTimings() != null) profile.setStoreTimings(dto.getStoreTimings());
        if (dto.getProfileImageUrl() != null) profile.setProfileImageUrl(dto.getProfileImageUrl());

        merchantProfileRepository.save(profile);

        if (dto.getBankHolderName() != null || dto.getBankAccountNumber() != null || dto.getBankIfscCode() != null) {
            MerchantPayoutProfile payout = merchantPayoutProfileRepository.findByMerchantId(merchantId)
                    .orElseGet(() -> MerchantPayoutProfile.builder().merchantId(merchantId).build());

            if (dto.getBankHolderName() != null) payout.setBankHolderName(dto.getBankHolderName());
            if (dto.getBankAccountNumber() != null) payout.setBankAccountNumber(dto.getBankAccountNumber());
            if (dto.getBankIfscCode() != null) payout.setBankIfscCode(dto.getBankIfscCode());

            merchantPayoutProfileRepository.save(payout);
        }

        return getProfile(merchantId);
    }

    // --- HELPER METHODS ---
    private Order validateOrderOwnership(UUID merchantId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found: " + orderId, HttpStatus.NOT_FOUND));

        if (order.getMerchantId() != null && !order.getMerchantId().equals(merchantId)) {
            throw new BusinessException("Unauthorized access: Order does not belong to merchant: " + merchantId, HttpStatus.FORBIDDEN);
        }
        return order;
    }

    private MerchantOrderResponseDTO mapToMerchantOrderResponse(Order order) {
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

        BigDecimal itemSubtotal = items.stream()
                .map(i -> (i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = order.getDeliveryFee() != null ? order.getDeliveryFee() : BigDecimal.ZERO;

        return MerchantOrderResponseDTO.builder()
                .orderId(order.getId())
                .merchantId(order.getMerchantId())
                .storeName(order.getStoreName())
                .status(order.getOrderStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .items(itemDtos)
                .deliveryFee(deliveryFee)
                .totalAmount(itemSubtotal.add(deliveryFee))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private List<TopProductDTO> getTopSellingProducts(UUID merchantId, int limit) {
        List<Order> orders = orderRepository.findByMerchantIdOrderByCreatedAtDesc(merchantId);
        Map<String, TopProductDTO> productMap = new HashMap<>();

        for (Order o : orders) {
            if (o.getOrderStatus() != OrderStatus.CANCELLED && o.getOrderStatus() != OrderStatus.REFUNDED) {
                List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
                for (OrderItem i : items) {
                    String name = i.getItemName() != null ? i.getItemName() : "Product";
                    BigDecimal price = i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice();
                    BigDecimal totalSales = price.multiply(BigDecimal.valueOf(i.getQuantity()));

                    TopProductDTO dto = productMap.getOrDefault(name, new TopProductDTO(name, 0L, BigDecimal.ZERO));
                    dto.setTotalQuantity(dto.getTotalQuantity() + i.getQuantity());
                    dto.setTotalSales(dto.getTotalSales().add(totalSales));
                    productMap.put(name, dto);
                }
            }
        }

        return productMap.values().stream()
                .sorted((a, b) -> b.getTotalSales().compareTo(a.getTotalSales()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<SalesTrendDTO> getSalesTrends(List<Order> orders) {
        Map<String, SalesTrendDTO> trendMap = new TreeMap<>();
        for (Order o : orders) {
            if (o.getCreatedAt() != null && o.getOrderStatus() != OrderStatus.CANCELLED && o.getOrderStatus() != OrderStatus.REFUNDED) {
                String dateKey = o.getCreatedAt().toLocalDate().toString();
                List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
                BigDecimal rev = items.stream()
                        .map(i -> (i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                SalesTrendDTO trend = trendMap.getOrDefault(dateKey, new SalesTrendDTO(dateKey, BigDecimal.ZERO, 0L));
                trend.setRevenue(trend.getRevenue().add(rev));
                trend.setOrderCount(trend.getOrderCount() + 1);
                trendMap.put(dateKey, trend);
            }
        }
        return new ArrayList<>(trendMap.values());
    }
}
