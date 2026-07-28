-- V56: Create Finance & Billing Engine Enterprise Addendum Tables (Multi-Entity Accounting, Cost Centres, Budgets, & Financial Controls)

-- 1. Finance Entities Table
CREATE TABLE IF NOT EXISTS finance_entities (
    id UUID PRIMARY KEY,
    entity_code VARCHAR(50) NOT NULL UNIQUE,
    entity_name VARCHAR(100) NOT NULL,
    country_code VARCHAR(10) NOT NULL DEFAULT 'IND',
    tax_registration_number VARCHAR(50),
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Finance Cost Centres Table
CREATE TABLE IF NOT EXISTS finance_cost_centres (
    id UUID PRIMARY KEY,
    cost_centre_code VARCHAR(50) NOT NULL UNIQUE,
    cost_centre_name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    manager_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Finance Budgets Table
CREATE TABLE IF NOT EXISTS finance_budgets (
    id UUID PRIMARY KEY,
    budget_code VARCHAR(50) NOT NULL UNIQUE,
    period_code VARCHAR(30) NOT NULL,
    allocated_amount NUMERIC(16,2) NOT NULL,
    spent_amount NUMERIC(16,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'APPROVED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Finance Financial Controls Table
CREATE TABLE IF NOT EXISTS finance_financial_controls (
    id UUID PRIMARY KEY,
    control_code VARCHAR(50) NOT NULL UNIQUE,
    control_name VARCHAR(100) NOT NULL,
    threshold_amount NUMERIC(14,2) NOT NULL,
    requires_dual_approval BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
