-- ============================================================
-- Canal Informatique - Work Orders Management System
-- PostgreSQL Setup Script
-- ============================================================
-- IMPORTANT: Run this script in the 'test' database
-- psql -U postgres -d test -f setup.sql

-- ============================================================
-- 1. CREATE USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. CREATE CLIENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS clients (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. CREATE INTERVENTIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS interventions (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50),
    status VARCHAR(50),
    location VARCHAR(255),
    client_id INTEGER REFERENCES clients(id),
    assigned_to INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 4. CREATE NOTIFICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 5. CREATE HISTORY TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS history (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(255),
    entity_type VARCHAR(50),
    entity_id INTEGER,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 6. DELETE OLD TEST DATA (OPTIONAL - uncomment to reset)
-- ============================================================
-- DELETE FROM users WHERE email IN ('test@canal-info.fr', 'user@canal-info.fr', 'admin@canal-info.fr');

-- ============================================================
-- 7. INSERT TEST USERS
-- ============================================================
-- NOTE: These use plain-text passwords for testing ONLY
-- In production, use BCrypt hashing through the application

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

-- ============================================================
-- 8. INSERT SAMPLE CLIENTS
-- ============================================================
INSERT INTO clients (company_name, phone, address, email)
VALUES ('ACME Services', '+33 1 44 55 66 77', '12 Rue de Paris, 75001 Paris', 'contact@acme.fr')
ON CONFLICT DO NOTHING;

INSERT INTO clients (company_name, phone, address, email)
VALUES ('Delta SARL', '+33 1 23 45 67 89', '8 Avenue République, 75011 Paris', 'support@delta.fr')
ON CONFLICT DO NOTHING;

INSERT INTO clients (company_name, phone, address, email)
VALUES ('Nova Industries', '+33 1 98 76 54 32', '44 Boulevard Haussmann, 75008 Paris', 'hello@nova.fr')
ON CONFLICT DO NOTHING;

-- ============================================================
-- 9. VERIFY DATA
-- ============================================================
SELECT 'Users in database:' AS info;
SELECT COUNT(*) as user_count FROM users;
SELECT email, name, role FROM users;

SELECT '' AS info;
SELECT 'Clients in database:' AS info;
SELECT COUNT(*) as client_count FROM clients;

-- ============================================================
-- TEST CREDENTIALS
-- ============================================================
-- Admin: admin@canal-info.fr / admin123
-- Receptionist: receptionist@canal-info.fr / recept123
-- Technician: technician@canal-info.fr / tech123
-- Technician 2: technician2@canal-info.fr / tech456

SELECT * FROM users WHERE email = 'admin@canal-info.fr';
