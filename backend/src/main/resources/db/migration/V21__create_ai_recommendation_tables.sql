-- V21: Create AI Recommendation & Personalization Engine Tables

-- 1. Customer Behaviour Tracking Table
CREATE TABLE IF NOT EXISTS customer_behaviour_events (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- PRODUCT_VIEW, STORE_VIEW, SEARCH, WISHLIST_ADD, CART_ADD, ORDER_PLACED
    product_id UUID,
    merchant_id UUID,
    category_id UUID,
    search_query VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_behaviour_user ON customer_behaviour_events(user_id);
CREATE INDEX IF NOT EXISTS idx_behaviour_type ON customer_behaviour_events(event_type);

-- 2. Customer Personalization Profiles Table
CREATE TABLE IF NOT EXISTS customer_personalization_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    preferred_category_ids TEXT,
    favorite_merchant_ids TEXT,
    average_order_value DECIMAL(12, 2) DEFAULT 0.00,
    total_orders_count INT DEFAULT 0,
    engagement_score DOUBLE PRECISION DEFAULT 1.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
