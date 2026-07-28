-- V68: Create Multi-Tenancy Platform Enterprise Addendum Tables (Enterprise Identity Federation, SCIM Directory Sync, License Allocations, & Data Residency)

-- 1. Tenant Identity Providers Table
CREATE TABLE IF NOT EXISTS tenant_identity_providers (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id),
    provider_name VARCHAR(100) NOT NULL,
    provider_type VARCHAR(30) NOT NULL DEFAULT 'OIDC', -- SAML2, OIDC, OKTA, AZURE_AD
    issuer_url VARCHAR(255) NOT NULL,
    client_id VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tip_tenant ON tenant_identity_providers(tenant_id);

-- 2. Tenant Directory Sync Table (SCIM 2.0)
CREATE TABLE IF NOT EXISTS tenant_directory_sync (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id),
    sync_source VARCHAR(100) NOT NULL DEFAULT 'OKTA_SCIM',
    total_users_synced INT NOT NULL DEFAULT 0,
    sync_status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- IN_PROGRESS, COMPLETED, FAILED
    last_synced_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tds_tenant ON tenant_directory_sync(tenant_id);

-- 3. Tenant License Allocations Table
CREATE TABLE IF NOT EXISTS tenant_license_allocations (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id),
    license_type VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL DEFAULT 50,
    allocated_seats INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tla_tenant ON tenant_license_allocations(tenant_id);

-- 4. Tenant Data Residency Table
CREATE TABLE IF NOT EXISTS tenant_data_residency (
    id UUID PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) UNIQUE,
    data_region VARCHAR(50) NOT NULL DEFAULT 'ap-south-1', -- ap-south-1, us-east-1, eu-central-1
    compliance_standard VARCHAR(100) NOT NULL DEFAULT 'GDPR_SOC2',
    encryption_key_arn VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
