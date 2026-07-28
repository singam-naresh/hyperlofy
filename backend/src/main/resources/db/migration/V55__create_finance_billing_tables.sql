-- V55: Create Finance & Billing Engine Tables (GST Invoicing, Credit Notes, Taxation, & Accounting Close)

-- 1. Finance Invoices Table
CREATE TABLE IF NOT EXISTS finance_invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    merchant_id UUID,
    invoice_type VARCHAR(30) NOT NULL, -- CUSTOMER_TAX_INVOICE, MERCHANT_COMMISSION_INVOICE, DRIVER_STATEMENT
    gross_amount NUMERIC(14,2) NOT NULL,
    discount_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    cgst_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    sgst_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    igst_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    net_payable_amount NUMERIC(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ISSUED', -- ISSUED, PAID, VOIDED, PARTIALLY_REFUNDED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_finv_order ON finance_invoices(order_id);
CREATE INDEX IF NOT EXISTS idx_finv_customer ON finance_invoices(customer_id);

-- 2. Finance Invoice Items Table
CREATE TABLE IF NOT EXISTS finance_invoice_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES finance_invoices(id),
    item_description VARCHAR(255) NOT NULL,
    hsn_sac_code VARCHAR(20) DEFAULT '998313',
    unit_price NUMERIC(14,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    subtotal NUMERIC(14,2) NOT NULL,
    tax_rate NUMERIC(5,2) NOT NULL DEFAULT 18.00,
    tax_amount NUMERIC(14,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_fitem_inv ON finance_invoice_items(invoice_id);

-- 3. Finance Credit Notes Table
CREATE TABLE IF NOT EXISTS finance_credit_notes (
    id UUID PRIMARY KEY,
    credit_note_number VARCHAR(100) NOT NULL UNIQUE,
    invoice_id UUID NOT NULL REFERENCES finance_invoices(id),
    reason VARCHAR(100) NOT NULL,
    refund_amount NUMERIC(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ISSUED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Finance Accounting Periods Table
CREATE TABLE IF NOT EXISTS finance_accounting_periods (
    id UUID PRIMARY KEY,
    period_code VARCHAR(30) NOT NULL UNIQUE, -- e.g., FY26-Q2, 2026-07
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_closed BOOLEAN DEFAULT FALSE,
    closed_at TIMESTAMP WITH TIME ZONE,
    closed_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
