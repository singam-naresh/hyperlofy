package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminSessionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminSessionAuditRepository extends JpaRepository<AdminSessionAudit, UUID> {
    List<AdminSessionAudit> findByAdminUserOrderByCreatedAtDesc(String adminUser);
}
