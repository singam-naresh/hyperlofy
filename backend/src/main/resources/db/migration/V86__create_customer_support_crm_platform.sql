-- V86: Create Enterprise Customer Support, CRM & Operations Platform Tables

-- 1. Support Tickets Table
CREATE TABLE IF NOT EXISTS support_tickets (
    id UUID PRIMARY KEY,
    ticket_code VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    order_id UUID,
    category VARCHAR(80) NOT NULL DEFAULT 'REFUND', -- REFUND, RETURN, REPLACEMENT, MISSING_PRODUCT, WRONG_PRODUCT, DAMAGED_PRODUCT, DELIVERY_DELAY
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, ASSIGNED, IN_PROGRESS, WAITING_CUSTOMER, ESCALATED, RESOLVED, CLOSED
    assigned_agent_id UUID,
    sla_due_time TIMESTAMP WITH TIME ZONE,
    is_sla_breached BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_st_code ON support_tickets(ticket_code);
CREATE INDEX IF NOT EXISTS idx_st_cust ON support_tickets(customer_id);
CREATE INDEX IF NOT EXISTS idx_st_agent ON support_tickets(assigned_agent_id);

-- 2. Support Ticket Messages Table (Live Chat & Internal Notes)
CREATE TABLE IF NOT EXISTS support_ticket_messages (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    sender_user_id UUID NOT NULL,
    sender_role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER, AGENT, SUPERVISOR, AI_BOT
    content TEXT NOT NULL,
    is_internal_note BOOLEAN NOT NULL DEFAULT FALSE,
    attachment_urls VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_stm_ticket ON support_ticket_messages(ticket_id);

-- 3. Refund Cases Table
CREATE TABLE IF NOT EXISTS refund_cases (
    id UUID PRIMARY KEY,
    refund_code VARCHAR(100) NOT NULL UNIQUE,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    order_id UUID NOT NULL,
    amount NUMERIC(16,2) NOT NULL,
    refund_reason VARCHAR(100) NOT NULL DEFAULT 'WRONG_PRODUCT',
    refund_method VARCHAR(50) NOT NULL DEFAULT 'WALLET', -- WALLET, ORIGINAL_PAYMENT, INSTANT_BANK
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED', -- REQUESTED, APPROVED, REJECTED, COMPLETED
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_rc_code ON refund_cases(refund_code);
CREATE INDEX IF NOT EXISTS idx_rc_ticket ON refund_cases(ticket_id);

-- 4. Return Cases Table
CREATE TABLE IF NOT EXISTS return_cases (
    id UUID PRIMARY KEY,
    return_code VARCHAR(100) NOT NULL UNIQUE,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    order_id UUID NOT NULL,
    pickup_status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, PICKED_UP, INSPECTED, REJECTED, COMPLETED
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_retc_code ON return_cases(return_code);

-- 5. Replacement Cases Table
CREATE TABLE IF NOT EXISTS replacement_cases (
    id UUID PRIMARY KEY,
    replacement_code VARCHAR(100) NOT NULL UNIQUE,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    order_id UUID NOT NULL,
    dispatch_status VARCHAR(50) NOT NULL DEFAULT 'PENDING_DISPATCH',
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_repc_code ON replacement_cases(replacement_code);

-- 6. Knowledge Articles Table
CREATE TABLE IF NOT EXISTS knowledge_articles (
    id UUID PRIMARY KEY,
    article_code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(80) NOT NULL DEFAULT 'CUSTOMER_FAQ', -- CUSTOMER_FAQ, AGENT_SOP, MERCHANT_SOP
    view_count INTEGER NOT NULL DEFAULT 0,
    useful_count INTEGER NOT NULL DEFAULT 0,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ka_code ON knowledge_articles(article_code);

-- 7. Customer CSAT & NPS Surveys Table
CREATE TABLE IF NOT EXISTS customer_csat_surveys (
    id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL REFERENCES support_tickets(id),
    customer_id UUID NOT NULL,
    csat_rating INTEGER NOT NULL DEFAULT 5, -- 1-5
    nps_score INTEGER NOT NULL DEFAULT 10, -- 0-10
    ces_score INTEGER NOT NULL DEFAULT 5, -- 1-7
    feedback TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ccs_ticket ON customer_csat_surveys(ticket_id);
