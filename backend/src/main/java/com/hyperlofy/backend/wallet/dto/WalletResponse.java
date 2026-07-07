package com.hyperlofy.backend.wallet.dto;

import com.hyperlofy.backend.wallet.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class WalletResponse {
    private UUID walletId;
    private UUID userId;
    private String userEmail;
    private BigDecimal balance;
    private List<TransactionResponse> transactions;

    @Data
    @Builder
    public static class TransactionResponse {
        private UUID id;
        private BigDecimal amount;
        private TransactionType transactionType;
        private UUID referenceId;
        private String description;
        private OffsetDateTime createdAt;
    }
}
