package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByOrderId(UUID orderId);
    List<Settlement> findByPayeeIdOrderByCreatedAtDesc(UUID payeeId);
}
