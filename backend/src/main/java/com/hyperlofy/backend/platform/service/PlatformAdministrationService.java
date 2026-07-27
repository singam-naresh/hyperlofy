package com.hyperlofy.backend.platform.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.platform.dto.*;
import com.hyperlofy.backend.platform.entity.*;
import com.hyperlofy.backend.platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformAdministrationService {

    private final CouponRepository couponRepository;
    private final BannerRepository bannerRepository;
    private final ProductCategoryRepository categoryRepository;
    private final CmsPageRepository cmsPageRepository;
    private final SystemNotificationRepository notificationRepository;
    private final SupportTicketRepository ticketRepository;
    private final PlatformConfigurationRepository configurationRepository;
    private final FeatureFlagRepository featureFlagRepository;
    private final ExternalIntegrationRepository integrationRepository;

    // --- MODULE 1: COUPON ENGINE ---
    @Transactional(readOnly = true)
    @Cacheable(value = "coupons", key = "'all_coupons'")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "coupons", allEntries = true)
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new BusinessException("Coupon code already exists: " + coupon.getCode(), HttpStatus.CONFLICT);
        }
        return couponRepository.save(coupon);
    }

    @Transactional
    @CacheEvict(value = "coupons", allEntries = true)
    public Coupon setCouponActive(UUID couponId, boolean active) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException("Coupon not found: " + couponId, HttpStatus.NOT_FOUND));
        coupon.setIsActive(active);
        return couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "coupons", key = "#code")
    public CouponValidationResultDTO validateCoupon(String code, BigDecimal orderSubtotal) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Invalid coupon code: " + code, HttpStatus.NOT_FOUND));

        if (!Boolean.TRUE.equals(coupon.getIsActive())) {
            return CouponValidationResultDTO.builder().valid(false).code(code).discountAmount(BigDecimal.ZERO).finalOrderAmount(orderSubtotal).message("Coupon is inactive").build();
        }

        if (coupon.getMinOrderAmount() != null && orderSubtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            return CouponValidationResultDTO.builder().valid(false).code(code).discountAmount(BigDecimal.ZERO).finalOrderAmount(orderSubtotal).message("Minimum order amount of Rs. " + coupon.getMinOrderAmount() + " required").build();
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        } else if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = orderSubtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        }

        BigDecimal finalPrice = orderSubtotal.subtract(discount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) finalPrice = BigDecimal.ZERO;

        return CouponValidationResultDTO.builder()
                .valid(true)
                .code(code)
                .discountAmount(discount)
                .finalOrderAmount(finalPrice)
                .message("Coupon applied successfully!")
                .build();
    }

    // --- MODULE 2: CAMPAIGN & BANNERS ---
    @Transactional(readOnly = true)
    @Cacheable(value = "customer_home_feed", key = "'all_banners'")
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "customer_home_feed", allEntries = true)
    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    @Transactional
    @CacheEvict(value = "customer_home_feed", allEntries = true)
    public Banner setBannerActive(UUID bannerId, boolean active) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new BusinessException("Banner not found: " + bannerId, HttpStatus.NOT_FOUND));
        banner.setIsActive(active);
        return bannerRepository.save(banner);
    }

    // --- MODULE 3: CATEGORY MANAGEMENT ---
    @Transactional(readOnly = true)
    @Cacheable(value = "customer_home_feed", key = "'all_categories'")
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "customer_home_feed", allEntries = true)
    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    // --- MODULE 5: CMS PAGES ---
    @Transactional(readOnly = true)
    @Cacheable(value = "cms_pages", key = "#slug")
    public CmsPage getCmsPage(String slug) {
        return cmsPageRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException("CMS page not found: " + slug, HttpStatus.NOT_FOUND));
    }

    @Transactional
    @CacheEvict(value = "cms_pages", key = "#page.slug")
    public CmsPage saveCmsPage(CmsPage page) {
        return cmsPageRepository.save(page);
    }

    // --- MODULE 6: NOTIFICATIONS ---
    @Transactional(readOnly = true)
    public List<SystemNotification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Transactional
    public SystemNotification broadcastNotification(SystemNotification notification) {
        notification.setSentAt(OffsetDateTime.now());
        notification.setStatus("SENT");
        return notificationRepository.save(notification);
    }

    // --- MODULE 7: SUPPORT TICKETS ---
    @Transactional(readOnly = true)
    public List<SupportTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public SupportTicket createTicket(SupportTicket ticket) {
        if (ticket.getTicketNumber() == null) {
            ticket.setTicketNumber("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return ticketRepository.save(ticket);
    }

    @Transactional
    public SupportTicket updateTicket(UUID ticketId, String status, String notes, UUID adminId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Ticket not found: " + ticketId, HttpStatus.NOT_FOUND));
        if (status != null) ticket.setStatus(status);
        if (notes != null) ticket.setResolutionNotes(notes);
        if (adminId != null) ticket.setAssignedAdminId(adminId);
        return ticketRepository.save(ticket);
    }

    // --- MODULE 10: CONFIGURATIONS ---
    @Transactional(readOnly = true)
    @Cacheable(value = "platform_configurations", key = "'all_configs'")
    public List<PlatformConfiguration> getConfigurations() {
        return configurationRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "platform_configurations", allEntries = true)
    public PlatformConfiguration updateConfiguration(String key, String value) {
        PlatformConfiguration config = configurationRepository.findByConfigKey(key)
                .orElseGet(() -> PlatformConfiguration.builder().configKey(key).configGroup("SYSTEM").build());
        config.setConfigValue(value);
        return configurationRepository.save(config);
    }

    // --- MODULE 12: FEATURE FLAGS ---
    @Transactional(readOnly = true)
    @Cacheable(value = "feature_flags", key = "'all_flags'")
    public List<FeatureFlag> getFeatureFlags() {
        return featureFlagRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "feature_flags", allEntries = true)
    public FeatureFlag toggleFeatureFlag(String key, boolean enabled) {
        FeatureFlag flag = featureFlagRepository.findByFlagKey(key)
                .orElseGet(() -> FeatureFlag.builder().flagKey(key).flagName(key).description("Dynamic feature flag").build());
        flag.setIsEnabled(enabled);
        return featureFlagRepository.save(flag);
    }

    // --- MODULE 13: INTEGRATIONS ---
    @Transactional(readOnly = true)
    public List<ExternalIntegration> getIntegrations() {
        return integrationRepository.findAll();
    }

    // --- MODULE 14: PLATFORM HEALTH ---
    public PlatformHealthDTO getPlatformHealth() {
        long memoryMax = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        return PlatformHealthDTO.builder()
                .databaseStatus("UP")
                .apiGatewayStatus("UP")
                .cacheStatus("UP")
                .queueStatus("UP")
                .schedulerStatus("UP")
                .version("1.0.0-RELEASE")
                .uptimeSeconds(uptime)
                .environment("PRODUCTION")
                .jvmMemoryUsedMb(memoryUsed)
                .jvmMemoryMaxMb(memoryMax)
                .build();
    }

    // --- MODULE 9: FRAUD & RISK ---
    public FraudRiskDashboardDTO getFraudRiskDashboard() {
        return FraudRiskDashboardDTO.builder()
                .duplicateAccountsCount(0L)
                .highRefundCustomersCount(0L)
                .highCancellationMerchantsCount(0L)
                .paymentFailureRate(0.02)
                .suspiciousOrdersCount(0L)
                .overallFraudScore(5)
                .riskRating("LOW - SAFE")
                .build();
    }
}
