-- V36: Create Buy For Me Engine Tables

-- 1. Buy For Me Orders Table
CREATE TABLE IF NOT EXISTS buy_for_me_orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL, -- MEDICINES, GROCERIES, FOOD, ELECTRONICS, GIFTS, HARDWARE
    quantity INT NOT NULL DEFAULT 1,
    preferred_brand VARCHAR(100),
    alternative_brand_allowed BOOLEAN DEFAULT TRUE,
    max_budget DOUBLE PRECISION NOT NULL,
    purchase_notes TEXT,
    delivery_address TEXT NOT NULL,
    delivery_latitude DOUBLE PRECISION NOT NULL,
    delivery_longitude DOUBLE PRECISION NOT NULL,
    delivery_instructions TEXT,
    preferred_delivery_time TIMESTAMP WITH TIME ZONE,
    priority VARCHAR(20) DEFAULT 'NORMAL', -- NORMAL, HIGH, EMERGENCY
    is_emergency BOOLEAN DEFAULT FALSE,
    is_medical BOOLEAN DEFAULT FALSE,
    is_age_restricted BOOLEAN DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    assigned_driver_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bfm_cust ON buy_for_me_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_bfm_driver ON buy_for_me_orders(assigned_driver_id);
CREATE INDEX IF NOT EXISTS idx_bfm_status ON buy_for_me_orders(status);

-- 2. Status History Table
CREATE TABLE IF NOT EXISTS buy_for_me_status_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    change_reason VARCHAR(255),
    changed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Images Table
CREATE TABLE IF NOT EXISTS buy_for_me_images (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    image_url VARCHAR(255) NOT NULL,
    image_type VARCHAR(30) NOT NULL, -- ITEM_REFERENCE, RECEIPT, PRODUCT_PROOF
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Purchase Proofs Table
CREATE TABLE IF NOT EXISTS buy_for_me_purchase_proofs (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    driver_id UUID NOT NULL,
    store_name VARCHAR(150) NOT NULL,
    store_address TEXT,
    invoice_number VARCHAR(100),
    bill_amount DOUBLE PRECISION NOT NULL,
    tax_amount DOUBLE PRECISION DEFAULT 0.0,
    discount_amount DOUBLE PRECISION DEFAULT 0.0,
    purchase_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    gps_latitude DOUBLE PRECISION,
    gps_longitude DOUBLE PRECISION,
    verification_notes TEXT,
    is_approved_by_customer BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Price Breakdown Table
CREATE TABLE IF NOT EXISTS buy_for_me_price_breakdown (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    product_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    delivery_fee DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    service_fee DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    platform_fee DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    tax DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    surge_pricing DOUBLE PRECISION DEFAULT 0.0,
    rain_surcharge DOUBLE PRECISION DEFAULT 0.0,
    distance_charges DOUBLE PRECISION DEFAULT 0.0,
    shopping_charges DOUBLE PRECISION DEFAULT 0.0,
    total_payable DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Approvals Table
CREATE TABLE IF NOT EXISTS buy_for_me_approvals (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    purchase_proof_id UUID NOT NULL REFERENCES buy_for_me_purchase_proofs(id),
    action VARCHAR(20) NOT NULL, -- APPROVED, REJECTED, CHANGES_REQUESTED
    rejection_reason TEXT,
    budget_adjustment DOUBLE PRECISION DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Cancellations Table
CREATE TABLE IF NOT EXISTS buy_for_me_cancellations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    cancelled_by VARCHAR(50) NOT NULL, -- CUSTOMER, DRIVER, ADMIN, SYSTEM
    cancellation_reason TEXT NOT NULL,
    cancellation_fee DOUBLE PRECISION DEFAULT 0.0,
    refund_amount DOUBLE PRECISION DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 8. Driver Assignments Table
CREATE TABLE IF NOT EXISTS buy_for_me_driver_assignments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    driver_id UUID NOT NULL,
    assignment_status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED', -- ASSIGNED, ACCEPTED, REJECTED, TIMED_OUT
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 9. Events Table
CREATE TABLE IF NOT EXISTS buy_for_me_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    event_type VARCHAR(50) NOT NULL, -- BuyForMeCreated, DriverAssigned, ShoppingStarted, ProofUploaded, CustomerApproved, Delivered, Cancelled
    payload TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
