-- V13: Create merchant_payout_profiles table for Merchant Settlement Engine (Phase 2)

CREATE TABLE merchant_payout_profiles (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    bank_holder_name VARCHAR(150) NOT NULL,
    bank_account_number VARCHAR(50) NOT NULL,
    bank_ifsc_code VARCHAR(20) NOT NULL,
    current_balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    cumulative_earnings DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_merchant_payout_profiles_merchant_id ON merchant_payout_profiles(merchant_id);
