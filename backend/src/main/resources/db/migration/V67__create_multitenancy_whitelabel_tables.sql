-- V67: Create Multi-Tenancy, White-Label & Enterprise Organization Tables (Tenants, Organizations, Custom Branding & Tenant Subscriptions)

-- 1. Tenants Table
CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY,
    tenant_code VARCHAR(100) NOT NULL UNIQUE,
    tenant_name VARCHAR(150) NOT NULL,
    domain_name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, PROVISIONING
    country_code VARCHAR(10) NOT NULL DEFAULT 'IN',
    currency_code VARCHAR(10) NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Organizations Table
CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id),
    org_name VARCHAR(150) NOT NULL,
    parent_org_id UUID,
    org_code VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_org_tenant ON organizations(tenant_id);

-- 3. Tenant Branding Table
CREATE TABLE IF NOT EXISTS tenant_branding (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) UNIQUE,
    primary_color VARCHAR(30) NOT NULL DEFAULT '#6200EE',
    secondary_color VARCHAR(30) NOT NULL DEFAULT '#03DAC6',
    logo_url VARCHAR(255),
    custom_css TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Tenant Subscriptions Table
CREATE TABLE IF NOT EXISTS tenant_subscriptions (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) UNIQUE,
    plan_name VARCHAR(50) NOT NULL DEFAULT 'ENTERPRISE', -- BASIC, PROFESSIONAL, ENTERPRISE
    monthly_fee NUMERIC(16,2) NOT NULL DEFAULT 9999.00,
    max_orders_per_month INT NOT NULL DEFAULT 100000,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
