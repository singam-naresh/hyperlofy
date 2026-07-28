package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminCaseRepository extends JpaRepository<AdminCase, UUID> {
    Optional<AdminCase> findByCaseNumber(String caseNumber);
    List<AdminCase> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
