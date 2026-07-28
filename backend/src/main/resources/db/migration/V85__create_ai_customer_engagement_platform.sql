-- V85: Create Enterprise AI Customer Engagement & Personalisation Platform Tables

-- 1. Customer Behaviour Profiles Table (CLV, Engagement Score, Preferred Categories, Purchase Frequency)
CREATE TABLE IF NOT EXISTS customer_behaviour_profiles (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE,
    engagement_score NUMERIC(5,2) NOT NULL DEFAULT 85.50,
    customer_lifetime_value NUMERIC(16,2) NOT NULL DEFAULT 12500.00,
    purchase_frequency_days NUMERIC(5,2) NOT NULL DEFAULT 7.50,
    preferred_categories VARCHAR(500) DEFAULT 'GROCERY,RESTAURANT',
    favorite_merchant_ids VARCHAR(1000),
    churn_probability NUMERIC(5,4) NOT NULL DEFAULT 0.0500,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_cbp_cust ON customer_behaviour_profiles(customer_id);

-- 2. Customer Segments Table (NEW, ACTIVE, LOYAL, VIP, HIGH_VALUE, PRICE_SENSITIVE, CHURN_RISK)
CREATE TABLE IF NOT EXISTS customer_segments (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    segment_name VARCHAR(80) NOT NULL, -- NEW, ACTIVE, LOYAL, VIP, HIGH_VALUE, PRICE_SENSITIVE, CHURN_RISK
    confidence_score NUMERIC(5,2) NOT NULL DEFAULT 98.00,
    assigned_by_model VARCHAR(100) NOT NULL DEFAULT 'gemini-customer-segmentation-v2',
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (customer_id, segment_name)
);

CREATE INDEX IF NOT EXISTS idx_cs_cust ON customer_segments(customer_id);
CREATE INDEX IF NOT EXISTS idx_cs_seg ON customer_segments(segment_name);

-- 3. Product Recommendations Table (AI Recommendations, Similarity Score, Model Version)
CREATE TABLE IF NOT EXISTS product_recommendations (
    id UUID PRIMARY KEY,
    recommendation_code VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    recommendation_type VARCHAR(80) NOT NULL DEFAULT 'COLLABORATIVE_FILTERING', -- COLLABORATIVE_FILTERING, SIMILAR_PURCHASE, RECENTLY_VIEWED, FREQUENTLY_BOUGHT_TOGETHER
    similarity_score NUMERIC(5,4) NOT NULL DEFAULT 0.9500,
    ai_model_version VARCHAR(100) NOT NULL DEFAULT 'gemini-recommendation-v3',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, VIEWED, ACCEPTED, REJECTED
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pr_code ON product_recommendations(recommendation_code);
CREATE INDEX IF NOT EXISTS idx_pr_cust ON product_recommendations(customer_id);

-- 4. Predictive Reorders Table (Groceries, Milk, Medicines Reorder Predictions)
CREATE TABLE IF NOT EXISTS predictive_reorders (
    id UUID PRIMARY KEY,
    prediction_code VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    product_id UUID NOT NULL,
    predicted_reorder_date TIMESTAMP WITH TIME ZONE NOT NULL,
    confidence_score NUMERIC(5,2) NOT NULL DEFAULT 94.50,
    reminder_schedule_cron VARCHAR(100) DEFAULT '0 9 * * *',
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, REMINDED, CONFIRMED, SKIPPED
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pro_code ON predictive_reorders(prediction_code);
CREATE INDEX IF NOT EXISTS idx_pro_cust ON predictive_reorders(customer_id);

-- 5. Notification Decisions Table (Smart Delivery Channel, Optimal Time, Priority, Explanation)
CREATE TABLE IF NOT EXISTS notification_decisions (
    id UUID PRIMARY KEY,
    decision_code VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    trigger_event VARCHAR(100) NOT NULL, -- CART_ABANDONMENT, REORDER_REMINDER, PRICE_DROP, WISHLIST_STOCK
    optimal_channel VARCHAR(50) NOT NULL DEFAULT 'PUSH_NOTIFICATION', -- PUSH_NOTIFICATION, EMAIL, SMS, WHATSAPP
    optimal_delivery_time TIMESTAMP WITH TIME ZONE NOT NULL,
    priority VARCHAR(30) NOT NULL DEFAULT 'HIGH', -- LOW, MEDIUM, HIGH, URGENT
    decision_explanation TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED', -- QUEUED, DELIVERED, FAILED
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_nd_code ON notification_decisions(decision_code);

-- 6. Marketing Campaigns Table (Campaign Automation, Target Audience, Execution Results)
CREATE TABLE IF NOT EXISTS marketing_campaigns (
    id UUID PRIMARY KEY,
    campaign_code VARCHAR(100) NOT NULL UNIQUE,
    campaign_name VARCHAR(150) NOT NULL,
    campaign_type VARCHAR(80) NOT NULL DEFAULT 'FESTIVAL_SALE', -- FESTIVAL_SALE, WINBACK, WELCOME, BIRTHDAY, FLASH_SALE
    target_segment VARCHAR(80) NOT NULL DEFAULT 'VIP_CUSTOMER',
    discount_coupon_code VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- DRAFT, ACTIVE, COMPLETED, PAUSED
    total_recipients INTEGER NOT NULL DEFAULT 50000,
    converted_count INTEGER NOT NULL DEFAULT 4250,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_mc_code ON marketing_campaigns(campaign_code);
