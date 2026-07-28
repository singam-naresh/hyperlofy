package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceInvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FinanceInvoiceItemRepository extends JpaRepository<FinanceInvoiceItem, UUID> {
    List<FinanceInvoiceItem> findByInvoiceId(UUID invoiceId);
}
