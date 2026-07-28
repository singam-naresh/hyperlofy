package com.hyperlofy.backend.settlement.repository;

import com.hyperlofy.backend.settlement.entity.SettlementTreasury;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementTreasuryRepository extends JpaRepository<SettlementTreasury, UUID> {
    Optional<SettlementTreasury> findByReservePoolName(String reservePoolName);
}
