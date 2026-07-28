-- V26: Create Merchant & Delivery Partner Management Platform Tables (Phase 2)

-- 1. Merchant Documents Table
CREATE TABLE IF NOT EXISTS merchant_documents (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- AADHAAR, PAN, GST, SHOP_LICENSE
    document_number VARCHAR(100) NOT NULL,
    document_url VARCHAR(255),
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_merchant_docs_merchant ON merchant_documents(merchant_id);

-- 2. Merchant Bank Accounts Table
CREATE TABLE IF NOT EXISTS merchant_bank_accounts (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(30) NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Stores Table
CREATE TABLE IF NOT EXISTS stores (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    store_name VARCHAR(150) NOT NULL,
    description TEXT,
    logo_url VARCHAR(255),
    banner_url VARCHAR(255),
    business_category VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    delivery_radius_km DOUBLE PRECISION NOT NULL DEFAULT 5.0,
    prep_time_minutes INT NOT NULL DEFAULT 20,
    store_status VARCHAR(30) NOT NULL DEFAULT 'CLOSED',
    is_accepting_orders BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_stores_merchant ON stores(merchant_id);
CREATE INDEX IF NOT EXISTS idx_stores_city ON stores(city);

-- 4. Store Timings Table
CREATE TABLE IF NOT EXISTS store_timings (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL,
    day_of_week VARCHAR(20) NOT NULL, -- MONDAY, TUESDAY...
    open_time VARCHAR(20) NOT NULL,
    close_time VARCHAR(20) NOT NULL,
    is_closed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_store_timings_store ON store_timings(store_id);

-- 5. Store Holidays Table
CREATE TABLE IF NOT EXISTS store_holidays (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL,
    holiday_date VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Vehicles Table
CREATE TABLE IF NOT EXISTS vehicles (
    id UUID PRIMARY KEY,
    delivery_partner_id UUID NOT NULL UNIQUE,
    vehicle_type VARCHAR(50) NOT NULL, -- BIKE, SCOOTER, BICYCLE, CAR
    vehicle_number VARCHAR(50) NOT NULL UNIQUE,
    rc_number VARCHAR(50),
    insurance_expiry_date VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Delivery Partner Documents Table
CREATE TABLE IF NOT EXISTS delivery_partner_documents (
    id UUID PRIMARY KEY,
    delivery_partner_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- AADHAAR, PAN, DRIVING_LICENSE, RC, INSURANCE
    document_number VARCHAR(100) NOT NULL,
    document_url VARCHAR(255),
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_partner_docs_partner ON delivery_partner_documents(delivery_partner_id);

-- 8. City Settings Table
CREATE TABLE IF NOT EXISTS city_settings (
    id UUID PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    max_delivery_radius_km DOUBLE PRECISION DEFAULT 15.0,
    operating_hours VARCHAR(100) DEFAULT '06:00-23:00',
    services_enabled TEXT DEFAULT 'MARKETPLACE,BUY_FOR_ME,PICKUP_DROP',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 9. Status History Tables
CREATE TABLE IF NOT EXISTS merchant_status_history (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason VARCHAR(255),
    changed_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS delivery_partner_status_history (
    id UUID PRIMARY KEY,
    delivery_partner_id UUID NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    reason VARCHAR(255),
    changed_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
