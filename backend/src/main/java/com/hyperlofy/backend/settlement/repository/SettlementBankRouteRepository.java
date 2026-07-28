package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.SettlementBankRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementBankRouteRepository extends JpaRepository<SettlementBankRoute, UUID> {
    List<SettlementBankRoute> findByIsActiveTrueOrderByPriorityOrderAsc();
}
