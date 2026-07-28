-- V28: Enterprise Missing Requirements Addendum Tables (Phase 2 & Phase 3)

-- 1. Product Status History Table
CREATE TABLE IF NOT EXISTS product_status_history (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason VARCHAR(255),
    changed_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_prod_status_history ON product_status_history(product_id);

-- 2. Inventory Transaction Ledger Table
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id UUID PRIMARY KEY,
    variant_id UUID NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- RESERVE, RELEASE, SALE, RETURN, DAMAGE, MANUAL_ADJUSTMENT
    quantity_changed INT NOT NULL,
    stock_before INT NOT NULL,
    stock_after INT NOT NULL,
    reason VARCHAR(255),
    performed_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_inv_tx_variant ON inventory_transactions(variant_id);

-- 3. Admin Notes Table
CREATE TABLE IF NOT EXISTS admin_notes (
    id UUID PRIMARY KEY,
    target_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL, -- MERCHANT, STORE, PRODUCT, PARTNER, CITY
    note_content TEXT NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_admin_notes_target ON admin_notes(target_id);

-- 4. Merchant Onboarding Checklist Table
CREATE TABLE IF NOT EXISTS merchant_onboarding_checklists (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    kyc_completed BOOLEAN DEFAULT FALSE,
    bank_verified BOOLEAN DEFAULT FALSE,
    store_created BOOLEAN DEFAULT FALSE,
    documents_uploaded BOOLEAN DEFAULT FALSE,
    admin_approved BOOLEAN DEFAULT FALSE,
    completion_percentage DOUBLE PRECISION DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
