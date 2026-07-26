package com.hyperlofy.backend.merchant.repository;

import com.hyperlofy.backend.merchant.entity.MerchantLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MerchantLedgerRepository extends JpaRepository<MerchantLedger, UUID> {
    List<MerchantLedger> findByStatus(String status);
    List<MerchantLedger> findByMerchantId(UUID merchantId);
    List<MerchantLedger> findBySettlementBatchId(UUID settlementBatchId);
}
