package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerWalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerWalletTransactionRepository extends JpaRepository<CustomerWalletTransaction, UUID> {
    List<CustomerWalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
