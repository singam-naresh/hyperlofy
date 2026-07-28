-- V46: Create Dynamic Pricing Engine Tables (Unified Pricing Authority & Dynamic Surge Calculation)

-- 1. Pricing Quotes Table
CREATE TABLE IF NOT EXISTS pricing_quotes (
    id UUID PRIMARY KEY,
    order_id UUID,
    service_type VARCHAR(40) NOT NULL, -- MARKETPLACE, BUY_FOR_ME, PICKUP_DROP
    service_level VARCHAR(30) NOT NULL DEFAULT 'STANDARD', -- EXPRESS, STANDARD, ECONOMY
    base_fare NUMERIC(12,2) NOT NULL,
    distance_charge NUMERIC(12,2) NOT NULL,
    time_charge NUMERIC(12,2) NOT NULL,
    surge_multiplier DOUBLE PRECISION DEFAULT 1.0,
    service_fee NUMERIC(12,2) DEFAULT 0.0,
    platform_fee NUMERIC(12,2) DEFAULT 0.0,
    tax_amount NUMERIC(12,2) DEFAULT 0.0,
    total_amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUOTE_CREATED',
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pquote_order ON pricing_quotes(order_id);
CREATE INDEX IF NOT EXISTS idx_pquote_status ON pricing_quotes(status);

-- 2. Pricing Quote Versions Table
CREATE TABLE IF NOT EXISTS pricing_quote_versions (
    id UUID PRIMARY KEY,
    quote_id UUID NOT NULL REFERENCES pricing_quotes(id),
    version_number INT NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    recalculation_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Pricing Rules Table
CREATE TABLE IF NOT EXISTS pricing_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    service_type VARCHAR(40) NOT NULL,
    base_fare NUMERIC(12,2) NOT NULL,
    min_fare NUMERIC(12,2) NOT NULL,
    per_km_rate NUMERIC(12,2) NOT NULL,
    per_minute_rate NUMERIC(12,2) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Pricing City Rules Table
CREATE TABLE IF NOT EXISTS pricing_city_rules (
    id UUID PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL UNIQUE,
    city_surge_cap DOUBLE PRECISION NOT NULL DEFAULT 3.0,
    base_multiplier DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Pricing Components Table
CREATE TABLE IF NOT EXISTS pricing_components (
    id UUID PRIMARY KEY,
    quote_id UUID NOT NULL REFERENCES pricing_quotes(id),
    component_name VARCHAR(50) NOT NULL,
    component_amount NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
