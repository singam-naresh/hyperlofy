package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.ExecutiveOperationsDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecutiveOperationsDashboardRepository extends JpaRepository<ExecutiveOperationsDashboard, UUID> {
    Optional<ExecutiveOperationsDashboard> findByDashboardKey(String dashboardKey);
}
