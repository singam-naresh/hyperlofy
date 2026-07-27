-- V18: Create Customer Experience Tables

-- 1. Customer Addresses Table
CREATE TABLE IF NOT EXISTS customer_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(50) DEFAULT 'HOME', -- HOME, OFFICE, OTHER
    address_line TEXT NOT NULL,
    landmark VARCHAR(150),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_customer_addresses_user ON customer_addresses(user_id);

-- 2. Customer Wishlists Table
CREATE TABLE IF NOT EXISTS customer_wishlists (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    product_id UUID,
    merchant_id UUID,
    folder_name VARCHAR(50) DEFAULT 'FAVORITES',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_customer_wishlists_user ON customer_wishlists(user_id);

-- 3. Customer Carts Table
CREATE TABLE IF NOT EXISTS customer_carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    merchant_id UUID,
    applied_coupon_code VARCHAR(50),
    discount_amount DECIMAL(12, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Customer Cart Items Table
CREATE TABLE IF NOT EXISTS customer_cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    item_instructions VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_customer_cart_items_cart ON customer_cart_items(cart_id);

-- 5. Customer Ratings & Reviews Table
CREATE TABLE IF NOT EXISTS customer_reviews (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    user_name VARCHAR(150),
    order_id UUID,
    merchant_id UUID,
    agent_id UUID,
    rating INT NOT NULL, -- 1 to 5
    review_text TEXT,
    review_type VARCHAR(30) DEFAULT 'STORE', -- STORE, PRODUCT, DELIVERY
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_customer_reviews_merchant ON customer_reviews(merchant_id);

-- 6. Customer Wallets Table
CREATE TABLE IF NOT EXISTS customer_wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance DECIMAL(12, 2) DEFAULT 0.00,
    reward_points INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Customer Wallet Transactions Table
CREATE TABLE IF NOT EXISTS customer_wallet_transactions (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- CREDIT, DEBIT, REFUND, CASHBACK
    description VARCHAR(255) NOT NULL,
    reference_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wallet_tx_wallet ON customer_wallet_transactions(wallet_id);
