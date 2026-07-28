package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceInvoiceRepository extends JpaRepository<FinanceInvoice, UUID> {
    Optional<FinanceInvoice> findByInvoiceNumber(String invoiceNumber);
    List<FinanceInvoice> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<FinanceInvoice> findByMerchantIdOrderByCreatedAtDesc(UUID merchantId);
}
