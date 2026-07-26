-- V11: Add merchant_id persistence to orders table for merchant settlement & identity tracking

ALTER TABLE orders ADD COLUMN merchant_id UUID;

CREATE INDEX idx_orders_merchant_id ON orders(merchant_id);
