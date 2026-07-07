package com.hyperlofy.backend.wallet.service;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.wallet.entity.TransactionType;
import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.entity.WalletTransaction;
import com.hyperlofy.backend.wallet.repository.WalletRepository;
import com.hyperlofy.backend.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Wallet createWalletForUser(UUID userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Wallet already exists for user: " + userId, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.NOT_FOUND));

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> createWalletForUser(userId));
    }

    @Transactional(readOnly = true)
    public List<WalletTransaction> getTransactionHistory(UUID userId) {
        Wallet wallet = getWalletByUserId(userId);
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @Transactional
    public WalletTransaction creditWallet(UUID userId, BigDecimal amount, TransactionType type, UUID referenceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Credit amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        log.info("Pessimistic locking wallet for update - User: {}, credit: {}", userId, amount);
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> {
                    createWalletForUser(userId);
                    return walletRepository.findByUserIdForUpdate(userId)
                            .orElseThrow(() -> new BusinessException("Failed to load initialized wallet", HttpStatus.INTERNAL_SERVER_ERROR));
                });

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(type)
                .referenceId(referenceId)
                .description(description)
                .build();

        log.info("Successfully credited user: {} with amount: {}. New balance: {}", userId, amount, wallet.getBalance());
        return walletTransactionRepository.save(tx);
    }

    @Transactional
    public WalletTransaction debitWallet(UUID userId, BigDecimal amount, TransactionType type, UUID referenceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Debit amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        log.info("Pessimistic locking wallet for update - User: {}, debit: {}", userId, amount);
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException("Active wallet not found for user: " + userId, HttpStatus.NOT_FOUND));

        // Double-spend prevention and strict balance boundary check
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient wallet funds for this operation", HttpStatus.PAYMENT_REQUIRED);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount.negate())
                .transactionType(type)
                .referenceId(referenceId)
                .description(description)
                .build();

        log.info("Successfully debited user: {} with amount: {}. New balance: {}", userId, amount, wallet.getBalance());
        return walletTransactionRepository.save(tx);
    }
}
