-- V54: Create Notifications Engine Enterprise Addendum Tables (Campaign Management, Customer Journeys, AI Optimization & Consent Governance)

-- 1. Notification Campaigns Table
CREATE TABLE IF NOT EXISTS notification_campaigns (
    id UUID PRIMARY KEY,
    campaign_name VARCHAR(100) NOT NULL UNIQUE,
    channel VARCHAR(30) NOT NULL,
    target_segment VARCHAR(100) NOT NULL,
    template_code VARCHAR(100) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT, APPROVED, LAUNCHED, COMPLETED
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Notification Journeys Table
CREATE TABLE IF NOT EXISTS notification_journeys (
    id UUID PRIMARY KEY,
    journey_name VARCHAR(100) NOT NULL,
    user_id UUID NOT NULL,
    current_step VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS', -- IN_PROGRESS, COMPLETED, CANCELLED
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_njourn_user ON notification_journeys(user_id);

-- 3. Notification Consent Table
CREATE TABLE IF NOT EXISTS notification_consent (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    marketing_consent BOOLEAN DEFAULT TRUE,
    transactional_consent BOOLEAN DEFAULT TRUE,
    consent_given_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Notification Engagement Table
CREATE TABLE IF NOT EXISTS notification_engagement (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    event_type VARCHAR(30) NOT NULL, -- OPENED, CLICKED, CONVERTED, BOVNCED
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
