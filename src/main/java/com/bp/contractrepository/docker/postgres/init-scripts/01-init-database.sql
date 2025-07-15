-- Contract Repository & Lifecycle Management System
-- Database Initialization Script
-- Banque Populaire - Morocco

-- Create additional databases if needed
CREATE DATABASE IF NOT EXISTS keycloak;

-- Create schemas for better organization
CREATE SCHEMA IF NOT EXISTS contract_mgmt;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS reporting;

-- Grant permissions to the application user
GRANT ALL PRIVILEGES ON SCHEMA contract_mgmt TO contract_user;
GRANT ALL PRIVILEGES ON SCHEMA audit TO contract_user;
GRANT ALL PRIVILEGES ON SCHEMA reporting TO contract_user;

-- Set default schema search path
ALTER USER contract_user SET search_path = contract_mgmt, audit, reporting, public;

-- Create extensions that might be needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";  -- For text search
CREATE EXTENSION IF NOT EXISTS "btree_gin"; -- For indexing

-- Initial database setup comments
COMMENT ON SCHEMA contract_mgmt IS 'Main contract management tables';
COMMENT ON SCHEMA audit IS 'Audit trail and history tables';
COMMENT ON SCHEMA reporting IS 'Reporting and analytics tables';

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'Contract Repository database initialized successfully!';
    RAISE NOTICE 'Schemas created: contract_mgmt, audit, reporting';
    RAISE NOTICE 'Extensions enabled: uuid-ossp, pg_trgm, btree_gin';
END $$;