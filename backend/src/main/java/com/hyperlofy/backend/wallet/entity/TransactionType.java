package com.hyperlofy.backend.wallet.entity;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    ORDER_PAYMENT,
    ESCROW_LOCK,
    ESCROW_RELEASE,
    REFUND,
    PAYOUT
}
