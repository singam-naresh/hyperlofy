package com.hyperlofy.backend.multitenancy.service;

import com.hyperlofy.backend.multitenancy.entity.TenantDataResidency;
import com.hyperlofy.backend.multitenancy.entity.TenantDirectorySync;
import com.hyperlofy.backend.multitenancy.entity.TenantIdentityProvider;
import com.hyperlofy.backend.multitenancy.entity.TenantLicenseAllocation;
import com.hyperlofy.backend.multitenancy.repository.TenantDataResidencyRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantDirectorySyncRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantIdentityProviderRepository;
import com.hyperlofy.backend.multitenancy.repository.TenantLicenseAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MultiTenantEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(MultiTenantEnterpriseService.class);

    private final TenantIdentityProviderRepository idpRepository;
    private final TenantDirectorySyncRepository scimRepository;
    private final TenantLicenseAllocationRepository licenseRepository;
    private final TenantDataResidencyRepository residencyRepository;

    @Transactional
    public TenantIdentityProvider configureIdentityProvider(UUID tenantId, String providerName, String providerType, String issuerUrl, String clientId) {
        log.info("[MULTI-TENANT ENTERPRISE] Configuring Enterprise SAML/OIDC IdP TenantId={}, Provider={}, Type={}", tenantId, providerName, providerType);

        TenantIdentityProvider idp = TenantIdentityProvider.builder()
                .tenantId(tenantId)
                .providerName(providerName)
                .providerType(providerType != null ? providerType : "OIDC")
                .issuerUrl(issuerUrl)
                .clientId(clientId)
                .status("ACTIVE")
                .build();

        return idpRepository.save(idp);
    }

    @Transactional
    public TenantDirectorySync triggerScimSync(UUID tenantId, String syncSource, Integer totalUsersSynced) {
        log.info("[MULTI-TENANT ENTERPRISE] Triggering SCIM 2.0 Directory Sync TenantId={}, Source={}, UsersSynced={}", tenantId, syncSource, totalUsersSynced);

        TenantDirectorySync sync = TenantDirectorySync.builder()
                .tenantId(tenantId)
                .syncSource(syncSource != null ? syncSource : "OKTA_SCIM")
                .totalUsersSynced(totalUsersSynced != null ? totalUsersSynced : 100)
                .syncStatus("COMPLETED")
                .lastSyncedAt(OffsetDateTime.now())
                .build();

        return scimRepository.save(sync);
    }

    @Transactional
    public TenantLicenseAllocation allocateLicenses(UUID tenantId, String licenseType, Integer totalSeats, Integer allocatedSeats) {
        log.info("[MULTI-TENANT ENTERPRISE] Allocating enterprise user licenses TenantId={}, Type={}, Seats={}", tenantId, licenseType, totalSeats);

        TenantLicenseAllocation allocation = TenantLicenseAllocation.builder()
                .tenantId(tenantId)
                .licenseType(licenseType)
                .totalSeats(totalSeats != null ? totalSeats : 50)
                .allocatedSeats(allocatedSeats != null ? allocatedSeats : 0)
                .status("ACTIVE")
                .build();

        return licenseRepository.save(allocation);
    }

    @Transactional
    public TenantDataResidency configureDataResidency(UUID tenantId, String dataRegion, String complianceStandard, String encryptionKeyArn) {
        log.info("[MULTI-TENANT ENTERPRISE] Configuring regional data residency & compliance TenantId={}, Region={}, Standard={}",
                tenantId, dataRegion, complianceStandard);

        TenantDataResidency residency = residencyRepository.findByTenantId(tenantId).orElseGet(() ->
                TenantDataResidency.builder()
                        .tenantId(tenantId)
                        .build()
        );

        if (dataRegion != null) residency.setDataRegion(dataRegion);
        if (complianceStandard != null) residency.setComplianceStandard(complianceStandard);
        if (encryptionKeyArn != null) residency.setEncryptionKeyArn(encryptionKeyArn);

        return residencyRepository.save(residency);
    }
}
