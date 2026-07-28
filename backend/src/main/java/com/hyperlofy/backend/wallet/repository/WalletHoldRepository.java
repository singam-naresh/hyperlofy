package com.hyperlofy.backend.wallet.repository;

import com.hyperlofy.backend.wallet.entity.WalletHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletHoldRepository extends JpaRepository<WalletHold, UUID> {
    Optional<WalletHold> findByWalletIdAndOrderIdAndStatus(UUID walletId, UUID orderId, String status);
}
