package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.GlobalCertificate;
import com.hyperlofy.backend.global.service.GlobalEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
@Tag(name = "Global Certificate & DNS Platform API", description = "SSL/TLS certificate lifecycle management, automated Let's Encrypt / ACM renewal, Route53 DNSSEC, and audit history")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DnsCertificateController {

    private final GlobalEnterpriseService enterpriseService;

    @PostMapping
    @Operation(summary = "Register Global SSL/TLS Certificate", description = "Registers global domain certificate with automated 90-day renewal and Route53 DNS integration.")
    public ResponseEntity<GlobalCertificate> registerCertificate(
            @RequestParam String domainName,
            @RequestParam(required = false) String caProvider,
            @RequestParam(required = false) String dnsProvider) {
        return ResponseEntity.ok(enterpriseService.registerCertificate(domainName, caProvider, dnsProvider));
    }

    @GetMapping
    @Operation(summary = "List Active Global Certificates", description = "Returns active SSL/TLS certificates, expiration dates, auto-renew status, and certificate authorities.")
    public ResponseEntity<List<GlobalCertificate>> getCertificates() {
        return ResponseEntity.ok(enterpriseService.getCertificates());
    }
}
