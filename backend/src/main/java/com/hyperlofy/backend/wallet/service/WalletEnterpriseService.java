package com.hyperlofy.backend.wallet.service;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(WalletEnterpriseService.class);

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet transferTreasuryFunds(UUID sourceWalletOwnerId, UUID targetWalletOwnerId, BigDecimal amount, String purpose) {
        log.info("[WALLET ENTERPRISE] Master Treasury Transfer: SourceId={}, TargetId={}, Amount={}, Purpose={}",
                sourceWalletOwnerId, targetWalletOwnerId, amount, purpose);

        Wallet source = walletRepository.findByOwnerId(sourceWalletOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Source wallet not found: " + sourceWalletOwnerId));

        Wallet target = walletRepository.findByOwnerId(targetWalletOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Target wallet not found: " + targetWalletOwnerId));

        if (source.getSpendableBalance().compareTo(amount) < 0) {
            log.warn("[WALLET ENTERPRISE] Insufficient treasury funds in source wallet");
            throw new IllegalStateException("Insufficient treasury reserve balance");
        }

        source.setSpendableBalance(source.getSpendableBalance().subtract(amount));
        target.setSpendableBalance(target.getSpendableBalance().add(amount));

        walletRepository.save(source);
        return walletRepository.save(target);
    }
}
