package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.LoginAudit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, java.util.UUID> {
    List<LoginAudit> findByEmailOrderByCreatedAtDesc(String email);
    
    // Count failures on an IP address within a short historical frame
    long countByIpAddressAndLoginStatusOrderByCreatedAtDesc(String ipAddress, String loginStatus);
    
    // Recent logs
    List<LoginAudit> findByEmailAndLoginStatus(String email, String loginStatus, Pageable pageable);
}
