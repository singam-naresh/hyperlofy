-- V16: Extend agent_profiles table for Delivery Partner Platform

ALTER TABLE agent_profiles
ADD COLUMN IF NOT EXISTS work_status VARCHAR(30) NOT NULL DEFAULT 'OFFLINE',
ADD COLUMN IF NOT EXISTS emergency_contact VARCHAR(50),
ADD COLUMN IF NOT EXISTS driving_licence VARCHAR(50),
ADD COLUMN IF NOT EXISTS rating DECIMAL(3, 2) NOT NULL DEFAULT 5.00;

CREATE INDEX IF NOT EXISTS idx_agent_profiles_work_status ON agent_profiles(work_status);
