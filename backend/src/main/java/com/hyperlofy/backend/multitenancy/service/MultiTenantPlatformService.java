package com.hyperlofy.backend.multitenancy.service;

import com.hyperlofy.backend.multitenancy.entity.Organization;
import com.hyperlofy.backend.multitenancy.entity.Tenant;
import com.hyperlofy.backend.multitenancy.entity.TenantBranding;
import com.hyperlofy.backend.multitenancy.entity.TenantSubscription;
import com.hyperlofy.backend.multitenancy.repository.OrganizationRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantBrandingRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiTenantPlatformService {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantPlatformService.class);

    private final TenantRepository tenantRepository;
    private final OrganizationRepository organizationRepository;
    private final TenantBrandingRepository brandingRepository;
    private final TenantSubscriptionRepository subscriptionRepository;

    @Transactional
    public Tenant provisionTenant(String tenantCode, String tenantName, String domainName, String countryCode, String currencyCode) {
        log.info("[MULTI-TENANT PLATFORM] Provisioning multi-tenant SaaS tenant Code={}, Name={}, Domain={}, Country={}",
                tenantCode, tenantName, domainName, countryCode);

        Tenant tenant = Tenant.builder()
                .tenantCode(tenantCode)
                .tenantName(tenantName)
                .domainName(domainName)
                .status("ACTIVE")
                .countryCode(countryCode != null ? countryCode : "IN")
                .currencyCode(currencyCode != null ? currencyCode : "INR")
                .build();

        return tenantRepository.save(tenant);
    }

    @Transactional
    public Organization createOrganization(UUID tenantId, String orgName, UUID parentOrgId) {
        log.info("[MULTI-TENANT PLATFORM] Registering enterprise organization hierarchy TenantId={}, OrgName={}, ParentId={}",
                tenantId, orgName, parentOrgId);

        String orgCode = "ORG-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        Organization org = Organization.builder()
                .tenantId(tenantId)
                .orgName(orgName)
                .parentOrgId(parentOrgId)
                .orgCode(orgCode)
                .build();

        return organizationRepository.save(org);
    }

    @Transactional
    public TenantBranding updateWhiteLabelBranding(UUID tenantId, String primaryColor, String secondaryColor, String logoUrl, String customCss) {
        log.info("[MULTI-TENANT PLATFORM] Updating white-label tenant branding TenantId={}, PrimaryColor={}", tenantId, primaryColor);

        TenantBranding branding = brandingRepository.findByTenantId(tenantId).orElseGet(() ->
                TenantBranding.builder()
                        .tenantId(tenantId)
                        .build()
        );

        if (primaryColor != null) branding.setPrimaryColor(primaryColor);
        if (secondaryColor != null) branding.setSecondaryColor(secondaryColor);
        if (logoUrl != null) branding.setLogoUrl(logoUrl);
        if (customCss != null) branding.setCustomCss(customCss);

        return brandingRepository.save(branding);
    }

    @Transactional
    public TenantSubscription configureSubscription(UUID tenantId, String planName, BigDecimal monthlyFee, Integer maxOrders) {
        log.info("[MULTI-TENANT PLATFORM] Configuring SaaS subscription TenantId={}, Plan={}, Fee={}, MaxOrders={}",
                tenantId, planName, monthlyFee, maxOrders);

        TenantSubscription subscription = subscriptionRepository.findByTenantId(tenantId).orElseGet(() ->
                TenantSubscription.builder()
                        .tenantId(tenantId)
                        .build()
        );

        subscription.setPlanName(planName != null ? planName : "ENTERPRISE");
        if (monthlyFee != null) subscription.setMonthlyFee(monthlyFee);
        if (maxOrders != null) subscription.setMaxOrdersPerMonth(maxOrders);
        subscription.setStatus("ACTIVE");

        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<Tenant> getAllActiveTenants() {
        return tenantRepository.findAll();
    }
}
