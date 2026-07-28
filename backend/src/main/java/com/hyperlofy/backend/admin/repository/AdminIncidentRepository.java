package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdminIncidentRepository extends JpaRepository<AdminIncident, UUID> {
    List<AdminIncident> findByStatusOrderByCreatedAtDesc(String status);
}
