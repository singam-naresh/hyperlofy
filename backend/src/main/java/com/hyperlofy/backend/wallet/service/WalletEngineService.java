package com.hyperlofy.backend.wallet.service;

import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.entity.WalletHold;
import com.hyperlofy.backend.wallet.entity.WalletLedgerEntry;
import com.hyperlofy.backend.wallet.repository.WalletHoldRepository;
import com.hyperlofy.backend.wallet.repository.WalletLedgerEntryRepository;
import com.hyperlofy.backend.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletEngineService {

    private static final Logger log = LoggerFactory.getLogger(WalletEngineService.class);

    private final WalletRepository walletRepository;
    private final WalletLedgerEntryRepository ledgerRepository;
    private final WalletHoldRepository holdRepository;

    @Transactional
    public Wallet createWallet(UUID ownerId, String ownerType) {
        log.info("[WALLET ENGINE] Creating wallet for OwnerId={}, Type={}", ownerId, ownerType);

        Wallet wallet = Wallet.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .currency("INR")
                .spendableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .promotionalBalance(BigDecimal.ZERO)
                .status("ACTIVE")
                .kycStatus("VERIFIED")
                .build();

        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet creditWallet(UUID ownerId, BigDecimal amount, String description, UUID referenceId) {
        log.info("[WALLET ENGINE] Crediting wallet OwnerId={}, Amount={}", ownerId, amount);

        Wallet wallet = walletRepository.findByOwnerId(ownerId).orElseGet(() -> createWallet(ownerId, "CUSTOMER"));
        BigDecimal newBalance = wallet.getSpendableBalance().add(amount).setScale(2, RoundingMode.HALF_UP);
        wallet.setSpendableBalance(newBalance);

        Wallet saved = walletRepository.save(wallet);

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletId(saved.getId())
                .entryType("CREDIT")
                .amount(amount)
                .balanceAfter(newBalance)
                .referenceId(referenceId)
                .description(description)
                .build();

        ledgerRepository.save(entry);
        return saved;
    }

    @Transactional
    public Wallet debitWallet(UUID ownerId, BigDecimal amount, String description, UUID referenceId) {
        log.info("[WALLET ENGINE] Debiting wallet OwnerId={}, Amount={}", ownerId, amount);

        Wallet wallet = walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner: " + ownerId));

        if (wallet.getSpendableBalance().compareTo(amount) < 0) {
            log.warn("[WALLET ENGINE] Insufficient spendable balance for OwnerId={}. Available={}, Requested={}",
                    ownerId, wallet.getSpendableBalance(), amount);
            throw new IllegalStateException("Insufficient wallet balance");
        }

        BigDecimal newBalance = wallet.getSpendableBalance().subtract(amount).setScale(2, RoundingMode.HALF_UP);
        wallet.setSpendableBalance(newBalance);

        Wallet saved = walletRepository.save(wallet);

        WalletLedgerEntry entry = WalletLedgerEntry.builder()
                .walletId(saved.getId())
                .entryType("DEBIT")
                .amount(amount)
                .balanceAfter(newBalance)
                .referenceId(referenceId)
                .description(description)
                .build();

        ledgerRepository.save(entry);
        return saved;
    }

    @Transactional
    public WalletHold placeEscrowHold(UUID ownerId, UUID orderId, BigDecimal amount) {
        log.info("[WALLET ENGINE] Placing escrow hold OwnerId={}, OrderId={}, Amount={}", ownerId, orderId, amount);

        Wallet wallet = walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner: " + ownerId));

        if (wallet.getSpendableBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient spendable balance to lock escrow hold");
        }

        wallet.setSpendableBalance(wallet.getSpendableBalance().subtract(amount));
        wallet.setReservedBalance(wallet.getReservedBalance().add(amount));
        walletRepository.save(wallet);

        WalletHold hold = WalletHold.builder()
                .walletId(wallet.getId())
                .orderId(orderId)
                .amount(amount)
                .status("LOCKED")
                .build();

        return holdRepository.save(hold);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByOwner(UUID ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner: " + ownerId));
    }

    @Transactional(readOnly = true)
    public List<WalletLedgerEntry> getLedgerEntries(UUID ownerId) {
        Wallet wallet = getWalletByOwner(ownerId);
        return ledgerRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }
}
