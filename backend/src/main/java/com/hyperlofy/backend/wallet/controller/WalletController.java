package com.hyperlofy.backend.wallet.controller;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import com.hyperlofy.backend.wallet.dto.WalletDepositRequest;
import com.hyperlofy.backend.wallet.dto.WalletResponse;
import com.hyperlofy.backend.wallet.entity.TransactionType;
import com.hyperlofy.backend.wallet.entity.Wallet;
import com.hyperlofy.backend.wallet.entity.WalletTransaction;
import com.hyperlofy.backend.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet() {
        User user = getCurrentAuthenticatedUser();
        Wallet wallet = walletService.getWalletByUserId(user.getId());
        List<WalletTransaction> txHistory = walletService.getTransactionHistory(user.getId());

        return ResponseEntity.ok(mapToWalletResponse(wallet, txHistory));
    }

    @PostMapping("/deposit")
    public ResponseEntity<WalletResponse> depositFunds(@RequestBody WalletDepositRequest request) {
        User user = getCurrentAuthenticatedUser();
        walletService.creditWallet(
                user.getId(),
                request.getAmount(),
                TransactionType.DEPOSIT,
                null,
                request.getDescription() != null ? request.getDescription() : "Manual wallet top-up"
        );

        Wallet wallet = walletService.getWalletByUserId(user.getId());
        List<WalletTransaction> txHistory = walletService.getTransactionHistory(user.getId());
        return ResponseEntity.ok(mapToWalletResponse(wallet, txHistory));
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User profile not found", HttpStatus.UNAUTHORIZED));
    }

    private WalletResponse mapToWalletResponse(Wallet wallet, List<WalletTransaction> txList) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUser().getId())
                .userEmail(wallet.getUser().getEmail())
                .balance(wallet.getBalance())
                .transactions(txList.stream().map(tx -> WalletResponse.TransactionResponse.builder()
                        .id(tx.getId())
                        .amount(tx.getAmount())
                        .transactionType(tx.getTransactionType())
                        .referenceId(tx.getReferenceId())
                        .description(tx.getDescription())
                        .createdAt(tx.getCreatedAt())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
