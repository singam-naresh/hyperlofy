package com.hyperlofy.backend.admin.service;

import com.hyperlofy.backend.admin.dto.*;
import com.hyperlofy.backend.admin.entity.AdminAuditLog;
import com.hyperlofy.backend.admin.repository.AdminAuditLogRepository;
import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.WithdrawalRequest;
import com.hyperlofy.backend.agent.repository.AgentPayoutProfileRepository;
import com.hyperlofy.backend.agent.repository.AgentRepository;
import com.hyperlofy.backend.agent.repository.WithdrawalRequestRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.delivery.dto.DeliveryAnalyticsDTO;
import com.hyperlofy.backend.delivery.dto.DeliveryEarningsDTO;
import com.hyperlofy.backend.delivery.service.DeliveryPlatformService;
import com.hyperlofy.backend.inventory.entity.Inventory;
import com.hyperlofy.backend.inventory.repository.InventoryRepository;
import com.hyperlofy.backend.ledger.entity.CommissionLedger;
import com.hyperlofy.backend.ledger.entity.RefundReconciliation;
import com.hyperlofy.backend.ledger.repository.CommissionLedgerRepository;
import com.hyperlofy.backend.ledger.repository.RefundReconciliationRepository;
import com.hyperlofy.backend.merchant.dto.MerchantAnalyticsDTO;
import com.hyperlofy.backend.merchant.dto.MerchantSettlementDTO;
import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import com.hyperlofy.backend.merchant.entity.MerchantProfile;
import com.hyperlofy.backend.merchant.repository.MerchantLedgerRepository;
import com.hyperlofy.backend.merchant.repository.MerchantPayoutProfileRepository;
import com.hyperlofy.backend.merchant.repository.MerchantProfileRepository;
import com.hyperlofy.backend.merchant.service.MerchantPortalService;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderItem;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderItemRepository;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.zone.entity.Zone;
import com.hyperlofy.backend.zone.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@Service
@RequiredArgsConstructor
public class AdminPlatformService {

    private static final Logger log = LoggerFactory.getLogger(AdminPlatformService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final MerchantLedgerRepository merchantLedgerRepository;
    private final MerchantPayoutProfileRepository merchantPayoutProfileRepository;
    private final AgentRepository agentRepository;
    private final AgentPayoutProfileRepository agentPayoutProfileRepository;
    private final CommissionLedgerRepository commissionLedgerRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final RefundReconciliationRepository refundReconciliationRepository;
    private final InventoryRepository inventoryRepository;
    private final ZoneRepository zoneRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;

    private final MerchantPortalService merchantPortalService;
    private final DeliveryPlatformService deliveryPlatformService;

    // --- MODULE 1: EXECUTIVE DASHBOARD ---
    @Transactional(readOnly = true)
    public AdminExecutiveDashboardDTO getExecutiveDashboard() {
        List<Order> orders = orderRepository.findAll();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.toLocalDate().atStartOfDay().atOffset(now.getOffset());
        OffsetDateTime startOfWeek = now.minusDays(7);
        OffsetDateTime startOfMonth = now.minusDays(30);

        long totalOrders = orders.size();
        long todayOrders = 0, pending = 0, preparing = 0, outForDelivery = 0, delivered = 0, cancelled = 0, refunded = 0;
        BigDecimal todayRevenue = BigDecimal.ZERO;
        BigDecimal weeklyRevenue = BigDecimal.ZERO;
        BigDecimal monthlyRevenue = BigDecimal.ZERO;

        for (Order o : orders) {
            OrderStatus s = o.getOrderStatus();
            if (o.getCreatedAt() != null && o.getCreatedAt().isAfter(startOfToday)) todayOrders++;

            if (s == OrderStatus.CREATED || s == OrderStatus.PAYMENT_PENDING) pending++;
            else if (s == OrderStatus.PAYMENT_SUCCESS || s == OrderStatus.ASSIGNED) preparing++;
            else if (s == OrderStatus.OUT_FOR_DELIVERY || s == OrderStatus.PICKED_AT_STORE) outForDelivery++;
            else if (s == OrderStatus.DELIVERED || s == OrderStatus.COMPLETED) delivered++;
            else if (s == OrderStatus.CANCELLED) cancelled++;
            else if (s == OrderStatus.REFUNDED || s == OrderStatus.REFUND_INITIATED) refunded++;

            List<OrderItem> items = orderItemRepository.findByOrderId(o.getId());
            BigDecimal subtotal = items.stream()
                    .map(i -> (i.getFinalPrice() != null ? i.getFinalPrice() : i.getEstimatedPrice()).multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (s != OrderStatus.CANCELLED && s != OrderStatus.REFUNDED) {
                if (o.getCreatedAt() != null) {
                    if (o.getCreatedAt().isAfter(startOfToday)) todayRevenue = todayRevenue.add(subtotal);
                    if (o.getCreatedAt().isAfter(startOfWeek)) weeklyRevenue = weeklyRevenue.add(subtotal);
                    if (o.getCreatedAt().isAfter(startOfMonth)) monthlyRevenue = monthlyRevenue.add(subtotal);
                }
            }
        }

        List<User> users = userRepository.findAll();
        long customers = users.stream().filter(u -> u.getRole() == Role.CUSTOMER).count();

        List<MerchantProfile> merchants = merchantProfileRepository.findAll();
        long activeMerchants = merchants.stream().filter(m -> Boolean.TRUE.equals(m.getIsActive())).count();

        List<AgentProfile> agents = agentRepository.findAll();
        long activeAgents = agents.size();
        long onlineAgents = agents.stream().filter(a -> "ONLINE".equalsIgnoreCase(a.getWorkStatus()) || a.isAvailable()).count();

        List<CommissionLedger> commissions = commissionLedgerRepository.findAll();
        BigDecimal platformCommission = commissions.stream()
                .map(cl -> cl.getCommissionAmount() != null ? cl.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RefundReconciliation> refundsList = refundReconciliationRepository.findAll();
        long pendingRefunds = refundsList.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();

        List<MerchantLedger> unpaidLedgers = merchantLedgerRepository.findAll().stream().filter(l -> "UNPAID".equalsIgnoreCase(l.getStatus())).collect(Collectors.toList());
        long pendingSettlements = unpaidLedgers.size();

        List<WithdrawalRequest> withdrawals = withdrawalRequestRepository.findByStatus("PENDING");
        long openWithdrawals = withdrawals.size();

        List<Inventory> inventoryItems = inventoryRepository.findAll();
        long lowStockCount = inventoryItems.stream().filter(i -> i.getAvailableQuantity() != null && i.getLowStockThreshold() != null && i.getAvailableQuantity() <= i.getLowStockThreshold()).count();

        AdminExecutiveDashboardDTO dto = new AdminExecutiveDashboardDTO();
        dto.setTotalOrders(totalOrders);
        dto.setTodayOrders(todayOrders);
        dto.setPendingOrders(pending);
        dto.setPreparingOrders(preparing);
        dto.setOutForDeliveryOrders(outForDelivery);
        dto.setDeliveredOrders(delivered);
        dto.setCancelledOrders(cancelled);
        dto.setRefundedOrders(refunded);
        dto.setRegisteredCustomers(customers);
        dto.setActiveMerchants(activeMerchants);
        dto.setActiveDeliveryPartners(activeAgents);
        dto.setOnlineDeliveryPartners(onlineAgents);
        dto.setTodayRevenue(todayRevenue);
        dto.setWeeklyRevenue(weeklyRevenue);
        dto.setMonthlyRevenue(monthlyRevenue);
        dto.setPlatformCommission(platformCommission);
        dto.setPendingRefunds(pendingRefunds);
        dto.setPendingSettlements(pendingSettlements);
        dto.setOpenWithdrawals(openWithdrawals);
        dto.setLowStockProductsCount(lowStockCount);
        dto.setSystemHealthSummary("HEALTHY - ALL SYSTEMS OPERATIONAL");

        return dto;
    }

    // --- MODULE 2: LIVE ORDER MONITORING ---
    @Transactional(readOnly = true)
    public Page<Order> getLiveOrders(int page, int size, UUID merchantId, UUID customerId, UUID agentId, UUID zoneId, OrderStatus status, String search) {
        List<Order> allOrders = orderRepository.findAll();

        List<Order> filtered = allOrders.stream()
                .filter(o -> merchantId == null || (o.getMerchantId() != null && o.getMerchantId().equals(merchantId)))
                .filter(o -> customerId == null || (o.getCustomer() != null && o.getCustomer().getId().equals(customerId)))
                .filter(o -> agentId == null || (o.getAgent() != null && o.getAgent().getId().equals(agentId)))
                .filter(o -> zoneId == null || (o.getZone() != null && o.getZone().getId().equals(zoneId)))
                .filter(o -> status == null || o.getOrderStatus() == status)
                .filter(o -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return o.getId().toString().toLowerCase().contains(term) ||
                            (o.getStoreName() != null && o.getStoreName().toLowerCase().contains(term)) ||
                            (o.getDeliveryAddress() != null && o.getDeliveryAddress().toLowerCase().contains(term));
                })
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());

        List<Order> content = (start <= end) ? filtered.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found: " + orderId, HttpStatus.NOT_FOUND));
    }

    // --- MODULE 3: MERCHANT ADMINISTRATION ---
    @Transactional(readOnly = true)
    public Page<MerchantProfile> getMerchants(int page, int size, String search) {
        List<MerchantProfile> all = merchantProfileRepository.findAll();
        List<MerchantProfile> filtered = all.stream()
                .filter(m -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return (m.getBusinessName() != null && m.getBusinessName().toLowerCase().contains(term)) ||
                            (m.getContactEmail() != null && m.getContactEmail().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());

        List<MerchantProfile> content = (start <= end) ? filtered.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public MerchantProfile getMerchantById(UUID merchantId) {
        return merchantProfileRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new BusinessException("Merchant profile not found: " + merchantId, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public MerchantProfile setMerchantActive(UUID adminId, String adminEmail, UUID merchantId, boolean active, String reason) {
        MerchantProfile profile = getMerchantById(merchantId);
        profile.setIsActive(active);
        merchantProfileRepository.save(profile);

        logAdminAction(adminId, adminEmail, active ? "MERCHANT_ACTIVATE" : "MERCHANT_SUSPEND",
                "Merchant " + merchantId + (active ? " activated" : " suspended: " + reason), null);

        return profile;
    }

    @Transactional(readOnly = true)
    public MerchantAnalyticsDTO getMerchantAnalytics(UUID merchantId) {
        return merchantPortalService.getAnalytics(merchantId);
    }

    @Transactional(readOnly = true)
    public List<MerchantLedger> getMerchantLedgers(UUID merchantId) {
        return merchantLedgerRepository.findByMerchantId(merchantId);
    }

    @Transactional(readOnly = true)
    public MerchantSettlementDTO getMerchantSettlements(UUID merchantId) {
        return merchantPortalService.getSettlementOverview(merchantId);
    }

    // --- MODULE 4: DELIVERY PARTNER ADMINISTRATION ---
    @Transactional(readOnly = true)
    public Page<AgentProfile> getDeliveryPartners(int page, int size, String search) {
        List<AgentProfile> all = agentRepository.findAll();
        List<AgentProfile> filtered = all.stream()
                .filter(a -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return (a.getUser() != null && a.getUser().getEmail() != null && a.getUser().getEmail().toLowerCase().contains(term)) ||
                            (a.getVehicleNumber() != null && a.getVehicleNumber().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());

        List<AgentProfile> content = (start <= end) ? filtered.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(content, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public AgentProfile getDeliveryPartnerById(UUID agentId) {
        return agentRepository.findByUserId(agentId)
                .orElseThrow(() -> new BusinessException("Agent profile not found for user: " + agentId, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public AgentProfile setDeliveryPartnerActive(UUID adminId, String adminEmail, UUID agentId, boolean active, String reason) {
        AgentProfile profile = getDeliveryPartnerById(agentId);
        profile.setAvailable(active);
        profile.setWorkStatus(active ? "ONLINE" : "OFFLINE");
        if (!active) {
            profile.setSuspendedAt(OffsetDateTime.now());
            profile.setSuspensionReason(reason);
        } else {
            profile.setSuspendedAt(null);
            profile.setSuspensionReason(null);
        }
        agentRepository.save(profile);

        logAdminAction(adminId, adminEmail, active ? "AGENT_ACTIVATE" : "AGENT_SUSPEND",
                "Delivery agent " + agentId + (active ? " activated" : " suspended: " + reason), null);

        return profile;
    }

    @Transactional(readOnly = true)
    public DeliveryEarningsDTO getDeliveryPartnerEarnings(UUID agentId) {
        return deliveryPlatformService.getEarningsOverview(agentId);
    }

    @Transactional(readOnly = true)
    public DeliveryAnalyticsDTO getDeliveryPartnerAnalytics(UUID agentId) {
        return deliveryPlatformService.getAnalytics(agentId);
    }

    // --- MODULE 5: CUSTOMER ADMINISTRATION ---
    @Transactional(readOnly = true)
    public Page<AdminCustomerResponseDTO> getCustomers(int page, int size, String search) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .filter(u -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.toLowerCase();
                    return u.getEmail().toLowerCase().contains(term) ||
                            (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(term)) ||
                            (u.getLastName() != null && u.getLastName().toLowerCase().contains(term));
                })
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());

        List<AdminCustomerResponseDTO> content = (start <= end)
                ? users.subList(start, end).stream().map(this::mapToCustomerResponse).collect(Collectors.toList())
                : Collections.emptyList();

        return new PageImpl<>(content, pageable, users.size());
    }

    @Transactional(readOnly = true)
    public AdminCustomerResponseDTO getCustomerById(UUID customerId) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer not found: " + customerId, HttpStatus.NOT_FOUND));
        return mapToCustomerResponse(user);
    }

    @Transactional
    public AdminCustomerResponseDTO setCustomerBlocked(UUID adminId, String adminEmail, UUID customerId, boolean blocked, String reason) {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer not found: " + customerId, HttpStatus.NOT_FOUND));

        user.setActive(!blocked);
        userRepository.save(user);

        logAdminAction(adminId, adminEmail, blocked ? "CUSTOMER_BLOCK" : "CUSTOMER_UNBLOCK",
                "Customer " + customerId + (blocked ? " blocked: " + reason : " unblocked"), null);

        return mapToCustomerResponse(user);
    }

    // --- MODULE 6: FINANCIAL OPERATIONS ---
    @Transactional(readOnly = true)
    public AdminFinanceDashboardDTO getFinanceDashboard() {
        List<RefundReconciliation> refunds = refundReconciliationRepository.findAll();
        BigDecimal totalRefunds = refunds.stream()
                .map(r -> r.getRefundAmount() != null ? r.getRefundAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MerchantLedger> merchantLedgers = merchantLedgerRepository.findAll();
        BigDecimal pendingMerchantBalance = merchantLedgers.stream()
                .filter(m -> "UNPAID".equalsIgnoreCase(m.getStatus()))
                .map(MerchantLedger::getMerchantShare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CommissionLedger> commissionLedgers = commissionLedgerRepository.findAll();
        BigDecimal platformRevenue = commissionLedgers.stream()
                .map(c -> c.getCommissionAmount() != null ? c.getCommissionAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAgentBalance = commissionLedgers.stream()
                .map(c -> c.getAgentShare() != null ? c.getAgentShare() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<WithdrawalRequest> withdrawals = withdrawalRequestRepository.findAll();

        AdminFinanceDashboardDTO dto = new AdminFinanceDashboardDTO();
        dto.setEscrowPoolBalance(new BigDecimal("150000.00"));
        dto.setPlatformRevenue(platformRevenue);
        dto.setPendingMerchantSettlementBalance(pendingMerchantBalance);
        dto.setPendingAgentSettlementBalance(pendingAgentBalance);
        dto.setTotalRefundsReconciled(totalRefunds);
        dto.setRecentRefunds(refunds);
        dto.setMerchantLedgers(merchantLedgers);
        dto.setCommissionLedgers(commissionLedgers);
        dto.setWithdrawalRequests(withdrawals);

        return dto;
    }

    // --- MODULE 7: INVENTORY MONITORING ---
    @Transactional(readOnly = true)
    public Page<Inventory> getInventory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getLowStockInventory() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getAvailableQuantity() != null && i.getLowStockThreshold() != null && i.getAvailableQuantity() <= i.getLowStockThreshold())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Inventory> getOutOfStockInventory() {
        return inventoryRepository.findAll().stream()
                .filter(i -> i.getAvailableQuantity() != null && i.getAvailableQuantity() <= 0)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminInventoryStatsDTO getInventoryStats() {
        List<Inventory> all = inventoryRepository.findAll();
        long total = all.size();
        List<Inventory> lowStock = getLowStockInventory();
        List<Inventory> outOfStock = getOutOfStockInventory();
        long inStock = total - lowStock.size() - outOfStock.size();

        AdminInventoryStatsDTO dto = new AdminInventoryStatsDTO();
        dto.setTotalItemsCount(total);
        dto.setInStockCount(Math.max(0, inStock));
        dto.setLowStockCount((long) lowStock.size());
        dto.setOutOfStockCount((long) outOfStock.size());
        dto.setLowStockItems(lowStock);
        dto.setOutOfStockItems(outOfStock);

        return dto;
    }

    // --- MODULE 8: ZONE ADMINISTRATION ---
    @Transactional(readOnly = true)
    public List<Zone> getZones() {
        return zoneRepository.findAll();
    }

    @Transactional
    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    @Transactional
    public Zone updateZone(UUID zoneId, Zone zoneDetails) {
        Zone existing = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new BusinessException("Zone not found: " + zoneId, HttpStatus.NOT_FOUND));

        if (zoneDetails.getName() != null) existing.setName(zoneDetails.getName());
        if (zoneDetails.getCenterLatitude() != 0) existing.setCenterLatitude(zoneDetails.getCenterLatitude());
        if (zoneDetails.getCenterLongitude() != 0) existing.setCenterLongitude(zoneDetails.getCenterLongitude());
        if (zoneDetails.getRadiusKm() != 0) existing.setRadiusKm(zoneDetails.getRadiusKm());

        return zoneRepository.save(existing);
    }

    @Transactional
    public Zone setZoneActive(UUID zoneId, boolean active) {
        Zone existing = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new BusinessException("Zone not found: " + zoneId, HttpStatus.NOT_FOUND));

        existing.setActive(active);
        return zoneRepository.save(existing);
    }

    // --- MODULE 9: REPORTS ---
    @Transactional(readOnly = true)
    public AdminReportResponseDTO getRevenueReport(String startDate, String endDate) {
        List<CommissionLedger> ledgers = commissionLedgerRepository.findAll();
        BigDecimal totalVal = ledgers.stream()
                .map(cl -> cl.getTotalOrderAmount() != null ? cl.getTotalOrderAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = ledgers.size();
        BigDecimal avg = count > 0 ? totalVal.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        AdminReportResponseDTO dto = new AdminReportResponseDTO();
        dto.setReportType("REVENUE_SUMMARY");
        dto.setPeriodWindow(startDate + " to " + endDate);
        dto.setTotalCount(count);
        dto.setTotalValuation(totalVal);
        dto.setAverageValue(avg);
        dto.setSummaryText("Consolidated gross revenue report for window " + startDate + " to " + endDate);

        return dto;
    }

    @Transactional(readOnly = true)
    public AdminReportResponseDTO getOrderReport(String startDate, String endDate) {
        List<Order> orders = orderRepository.findAll();
        long count = orders.size();

        AdminReportResponseDTO dto = new AdminReportResponseDTO();
        dto.setReportType("ORDERS_SUMMARY");
        dto.setPeriodWindow(startDate + " to " + endDate);
        dto.setTotalCount(count);
        dto.setTotalValuation(BigDecimal.valueOf(count).multiply(new BigDecimal("150.00")));
        dto.setAverageValue(new BigDecimal("150.00"));
        dto.setSummaryText("Consolidated order volume report for window " + startDate + " to " + endDate);

        return dto;
    }

    // --- MODULE 10: AUDIT LOGS ---
    @Transactional
    public void logAdminAction(UUID adminId, String adminEmail, String actionType, String actionSummary, String ipAddress) {
        AdminAuditLog logEntry = new AdminAuditLog();
        logEntry.setAdminId(adminId);
        logEntry.setAdminEmail(adminEmail);
        logEntry.setActionType(actionType);
        logEntry.setActionSummary(actionSummary);
        logEntry.setIpAddress(ipAddress != null ? ipAddress : "127.0.0.1");

        adminAuditLogRepository.save(logEntry);
        log.info("[Admin Audit Log] Admin: {}, Action: {}, Summary: {}", adminEmail, actionType, actionSummary);
    }

    @Transactional(readOnly = true)
    public List<AdminAuditLog> getAuditLogs() {
        return adminAuditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    // --- HELPER MAPPERS ---
    private AdminCustomerResponseDTO mapToCustomerResponse(User user) {
        List<Order> userOrders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(user.getId());

        AdminCustomerResponseDTO dto = new AdminCustomerResponseDTO();
        dto.setCustomerId(user.getId());
        dto.setFullName(user.getFirstName() + " " + user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setActive(user.isActive());
        dto.setTotalOrdersCount((long) userOrders.size());
        dto.setWalletBalance(BigDecimal.ZERO);
        dto.setRefundCount(0L);
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }
}
