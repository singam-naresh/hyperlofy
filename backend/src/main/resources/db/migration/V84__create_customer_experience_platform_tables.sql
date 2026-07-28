-- V84: Create Enterprise Customer Experience Platform Tables (Reviews, Ratings, Comments, Replies, Reactions, Reports & Reputation)

-- 1. Customer Reviews Table (Product, Merchant, Store, Delivery & Service Reviews)
CREATE TABLE IF NOT EXISTS customer_reviews (
    id UUID PRIMARY KEY,
    review_code VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    product_id UUID,
    merchant_id UUID,
    delivery_partner_id UUID,
    order_id UUID,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    rating NUMERIC(3,2) NOT NULL DEFAULT 5.00, -- 1.00 to 5.00
    is_verified_purchase BOOLEAN DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'APPROVED', -- PENDING_MODERATION, APPROVED, REJECTED, FLAGGED
    ai_trust_score NUMERIC(5,2) NOT NULL DEFAULT 98.50,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    media_urls VARCHAR(1000), -- JSON array or CSV of uploaded image/video URLs
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_cr_code ON customer_reviews(review_code);
CREATE INDEX IF NOT EXISTS idx_cr_cust ON customer_reviews(customer_id);
CREATE INDEX IF NOT EXISTS idx_cr_prod ON customer_reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_cr_merch ON customer_reviews(merchant_id);
CREATE INDEX IF NOT EXISTS idx_cr_status ON customer_reviews(status);

-- 2. Review Ratings Breakdown Table (Quality, Packaging, Delivery, Value, Merchant Communication)
CREATE TABLE IF NOT EXISTS review_ratings (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL REFERENCES customer_reviews(id),
    quality_rating NUMERIC(3,2) DEFAULT 5.00,
    packaging_rating NUMERIC(3,2) DEFAULT 5.00,
    delivery_rating NUMERIC(3,2) DEFAULT 5.00,
    value_rating NUMERIC(3,2) DEFAULT 5.00,
    communication_rating NUMERIC(3,2) DEFAULT 5.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_rr_review ON review_ratings(review_id);

-- 3. Review Replies Table (Merchant & Support Responses to Customer Reviews)
CREATE TABLE IF NOT EXISTS review_replies (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL REFERENCES customer_reviews(id),
    replier_user_id UUID NOT NULL,
    replier_role VARCHAR(50) NOT NULL DEFAULT 'MERCHANT', -- MERCHANT, SUPPORT, ADMIN
    content TEXT NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_rrp_review ON review_replies(review_id);

-- 4. Review Reactions Table (Helpful Votes, Likes, Emoji Reactions)
CREATE TABLE IF NOT EXISTS review_reactions (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL REFERENCES customer_reviews(id),
    user_id UUID NOT NULL,
    reaction_type VARCHAR(30) NOT NULL DEFAULT 'HELPFUL', -- HELPFUL, LIKE, LOVE, FUNNY, DISAGREE
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (review_id, user_id, reaction_type)
);

CREATE INDEX IF NOT EXISTS idx_rrx_review ON review_reactions(review_id);

-- 5. Review Reports Table (Abuse, Spam, Fake Review, Offensive Language Reports)
CREATE TABLE IF NOT EXISTS review_reports (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL REFERENCES customer_reviews(id),
    reporter_user_id UUID NOT NULL,
    reason VARCHAR(80) NOT NULL, -- SPAM, FAKE_REVIEW, OFFENSIVE_LANGUAGE, HATE_SPEECH, COPYRIGHT
    comments TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, INVESTIGATED, DISMISSED, ACTIONED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_rrpt_review ON review_reports(review_id);

-- 6. Customer Reputations Table (Customer Reputation Score, Badges, Trust Level)
CREATE TABLE IF NOT EXISTS customer_reputations (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE,
    reputation_score NUMERIC(5,2) NOT NULL DEFAULT 95.00,
    verified_purchase_ratio NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    helpful_votes_received INTEGER NOT NULL DEFAULT 0,
    badge_level VARCHAR(50) NOT NULL DEFAULT 'GOLD_REVIEWER', -- BRONZE, SILVER, GOLD, ELITE, TOP_CONTRIBUTOR
    community_trust_level VARCHAR(50) NOT NULL DEFAULT 'HIGHLY_TRUSTED',
    total_reviews_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_cr_cust_id ON customer_reputations(customer_id);

-- 7. Merchant Reputations Table (Merchant Avg Rating, CSAT Score, Complaint & Return Ratios)
CREATE TABLE IF NOT EXISTS merchant_reputations (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    average_rating NUMERIC(3,2) NOT NULL DEFAULT 4.85,
    total_reviews_count INTEGER NOT NULL DEFAULT 0,
    csat_score_percent NUMERIC(5,2) NOT NULL DEFAULT 96.50,
    avg_response_time_hours NUMERIC(5,2) NOT NULL DEFAULT 2.40,
    complaint_ratio_percent NUMERIC(5,2) NOT NULL DEFAULT 0.80,
    ai_trust_score NUMERIC(5,2) NOT NULL DEFAULT 99.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_mr_merch_id ON merchant_reputations(merchant_id);
