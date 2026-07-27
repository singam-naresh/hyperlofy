package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, UUID> {
    List<AdminAuditLog> findByAdminIdOrderByCreatedAtDesc(UUID adminId);
    List<AdminAuditLog> findByActionTypeOrderByCreatedAtDesc(String actionType);
    List<AdminAuditLog> findAllByOrderByCreatedAtDesc();
}
