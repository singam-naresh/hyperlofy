-- Create Zones Table
CREATE TABLE zones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    center_latitude DOUBLE PRECISION NOT NULL,
    center_longitude DOUBLE PRECISION NOT NULL,
    radius_km DOUBLE PRECISION NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_zones_name ON zones(name);
CREATE INDEX idx_zones_active ON zones(is_active);

-- Create Pricing Slabs Table
CREATE TABLE pricing_slabs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    zone_id UUID NOT NULL REFERENCES zones(id) ON DELETE CASCADE,
    min_distance_km DOUBLE PRECISION NOT NULL,
    max_distance_km DOUBLE PRECISION NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    per_km_price DECIMAL(10,2) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_pricing_slabs_zone ON pricing_slabs(zone_id);

-- Create Orders Table
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    agent_id UUID REFERENCES users(id) ON DELETE SET NULL,
    zone_id UUID NOT NULL REFERENCES zones(id),
    store_name VARCHAR(150) NOT NULL,
    store_latitude DOUBLE PRECISION NOT NULL,
    store_longitude DOUBLE PRECISION NOT NULL,
    delivery_address TEXT NOT NULL,
    delivery_latitude DOUBLE PRECISION NOT NULL,
    delivery_longitude DOUBLE PRECISION NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL,
    delivery_fee DECIMAL(10,2) NOT NULL,
    items_desc TEXT NOT NULL,
    order_status VARCHAR(40) DEFAULT 'CREATED' NOT NULL,
    otp_code VARCHAR(6),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_agent ON orders(agent_id);
CREATE INDEX idx_orders_status ON orders(order_status);

-- Seed pre-configured Zone Examples: Tirupati, Bangalore, Chennai as requested
INSERT INTO zones (name, center_latitude, center_longitude, radius_km, is_active) VALUES
('Tirupati', 13.6288, 79.4192, 15.0, true),
('Bangalore', 12.9716, 77.5946, 25.0, true),
('Chennai', 13.0827, 80.2707, 30.0, true);

-- Seed default slab pricing for each zone
-- Zone Tirupati
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 0.0, 2.0, 30.00, 0.00 FROM zones WHERE name = 'Tirupati';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 2.0, 5.0, 50.00, 5.00 FROM zones WHERE name = 'Tirupati';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 5.0, 999.0, 80.00, 10.00 FROM zones WHERE name = 'Tirupati';

-- Zone Bangalore
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 0.0, 3.0, 40.00, 0.00 FROM zones WHERE name = 'Bangalore';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 3.0, 8.0, 70.00, 6.00 FROM zones WHERE name = 'Bangalore';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 8.0, 999.0, 120.00, 12.00 FROM zones WHERE name = 'Bangalore';

-- Zone Chennai
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 0.0, 3.0, 45.00, 0.00 FROM zones WHERE name = 'Chennai';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 3.0, 10.0, 75.00, 7.00 FROM zones WHERE name = 'Chennai';
INSERT INTO pricing_slabs (zone_id, min_distance_km, max_distance_km, base_price, per_km_price)
SELECT id, 10.0, 999.0, 130.00, 15.00 FROM zones WHERE name = 'Chennai';
