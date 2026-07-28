-- V47: Dynamic Pricing Engine Enterprise Addendum Tables (Promotions, Coupons, Tax Rules, & AI Recommendations)

-- 1. Pricing AI Recommendations Table
CREATE TABLE IF NOT EXISTS pricing_ai_recommendations (
    id UUID PRIMARY KEY,
    service_type VARCHAR(40) NOT NULL,
    recommended_base_fare NUMERIC(12,2) NOT NULL,
    recommended_surge_multiplier DOUBLE PRECISION NOT NULL,
    confidence_score DOUBLE PRECISION NOT NULL DEFAULT 95.0,
    rationale TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Pricing Promotions Table
CREATE TABLE IF NOT EXISTS pricing_promotions (
    id UUID PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FLAT
    discount_value NUMERIC(12,2) NOT NULL,
    max_discount_amount NUMERIC(12,2),
    min_order_amount NUMERIC(12,2) DEFAULT 0.0,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Pricing Coupons Table
CREATE TABLE IF NOT EXISTS pricing_coupons (
    id UUID PRIMARY KEY,
    coupon_code VARCHAR(50) NOT NULL UNIQUE,
    promotion_id UUID NOT NULL REFERENCES pricing_promotions(id),
    max_redemptions INT DEFAULT 1000,
    current_redemptions INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Pricing Coupon Redemptions Table
CREATE TABLE IF NOT EXISTS pricing_coupon_redemptions (
    id UUID PRIMARY KEY,
    coupon_id UUID NOT NULL REFERENCES pricing_coupons(id),
    user_id UUID NOT NULL,
    order_id UUID NOT NULL,
    discount_amount NUMERIC(12,2) NOT NULL,
    redeemed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pcred_user ON pricing_coupon_redemptions(user_id);

-- 5. Pricing Tax Rules Table
CREATE TABLE IF NOT EXISTS pricing_tax_rules (
    id UUID PRIMARY KEY,
    tax_name VARCHAR(50) NOT NULL UNIQUE,
    tax_rate_percent NUMERIC(5,2) NOT NULL DEFAULT 18.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Pricing Governance Table
CREATE TABLE IF NOT EXISTS pricing_governance (
    id UUID PRIMARY KEY,
    change_type VARCHAR(50) NOT NULL,
    proposed_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL', -- PENDING_APPROVAL, APPROVED, REJECTED
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
