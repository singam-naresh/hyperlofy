-- V15: Create merchant_profiles table for Merchant Business Portal

CREATE TABLE merchant_profiles (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    business_name VARCHAR(150) NOT NULL,
    contact_email VARCHAR(150),
    contact_phone VARCHAR(30),
    store_timings VARCHAR(100),
    profile_image_url VARCHAR(255),
    rating DECIMAL(3, 2) NOT NULL DEFAULT 5.00,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_merchant_profiles_merchant_id ON merchant_profiles(merchant_id);
