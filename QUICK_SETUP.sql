-- QUICK FIX: Paste this into PostgreSQL to enable login NOW
-- Run in: pgAdmin Query Editor or: psql -U postgres -d test

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clear old test data (optional - uncomment to reset)
-- TRUNCATE TABLE users CASCADE;

-- Insert test users - THESE ARE THE LOGIN CREDENTIALS
INSERT INTO users (email, password, name, role) 
VALUES ('admin@canal-info.fr', 'admin123', 'Admin Canal', 'ADMINISTRATOR')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password, name, role) 
VALUES ('receptionist@canal-info.fr', 'recept123', 'Nadia Ben', 'RECEPTIONIST')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password, name, role) 
VALUES ('technician@canal-info.fr', 'tech123', 'Karim Ali', 'TECHNICIAN')
ON CONFLICT (email) DO NOTHING;

INSERT INTO users (email, password, name, role) 
VALUES ('technician2@canal-info.fr', 'tech456', 'Jean Martin', 'TECHNICIAN')
ON CONFLICT (email) DO NOTHING;

-- Verify users were created
SELECT 'SUCCESS! ' || COUNT(*) || ' users ready to login' FROM users;
SELECT email, name, role FROM users ORDER BY email;
