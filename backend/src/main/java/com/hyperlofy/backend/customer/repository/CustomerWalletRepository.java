package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerWalletRepository extends JpaRepository<CustomerWallet, UUID> {
    Optional<CustomerWallet> findByUserId(UUID userId);
}
