-- V12: Create merchant_ledgers table for Merchant Settlement Engine (Phase 1)

CREATE TABLE merchant_ledgers (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    order_id UUID NOT NULL,
    item_subtotal DECIMAL(12, 2) NOT NULL,
    merchant_share DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    settlement_batch_id UUID NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_merchant_ledgers_merchant_id ON merchant_ledgers(merchant_id);
CREATE INDEX idx_merchant_ledgers_order_id ON merchant_ledgers(order_id);
CREATE INDEX idx_merchant_ledgers_status ON merchant_ledgers(status);
