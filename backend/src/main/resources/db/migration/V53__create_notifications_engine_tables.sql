-- V53: Create Notifications & Communications Engine Tables (Multi-Channel Push, SMS, Email, & WhatsApp)

-- 1. Notification Messages Table
CREATE TABLE IF NOT EXISTS notification_messages (
    id UUID PRIMARY KEY,
    recipient_id UUID NOT NULL,
    channel VARCHAR(30) NOT NULL, -- PUSH, SMS, EMAIL, WHATSAPP, IN_APP
    template_code VARCHAR(100),
    title VARCHAR(255),
    body TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED', -- QUEUED, SENT, DELIVERED, READ, FAILED
    provider_name VARCHAR(50),
    delivery_attempts INT NOT NULL DEFAULT 0,
    delivered_at TIMESTAMP WITH TIME ZONE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_nmsg_recipient ON notification_messages(recipient_id);
CREATE INDEX IF NOT EXISTS idx_nmsg_status ON notification_messages(status);

-- 2. Notification Templates Table
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY,
    template_code VARCHAR(100) NOT NULL UNIQUE,
    channel VARCHAR(30) NOT NULL,
    subject_template VARCHAR(255),
    body_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Notification Preferences Table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    push_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT TRUE,
    email_enabled BOOLEAN DEFAULT TRUE,
    whatsapp_enabled BOOLEAN DEFAULT TRUE,
    quiet_hours_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Notification Providers Table
CREATE TABLE IF NOT EXISTS notification_providers (
    id UUID PRIMARY KEY,
    provider_name VARCHAR(50) NOT NULL UNIQUE, -- FCM, TWILIO, MSG91, SENDGRID, WHATSAPP
    channel VARCHAR(30) NOT NULL,
    priority INT NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    health_status VARCHAR(30) NOT NULL DEFAULT 'HEALTHY',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
