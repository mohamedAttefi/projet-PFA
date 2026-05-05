-- DIAGNOSTIC SCRIPT - Run this first to check your database status
-- Copy-paste this entire script into pgAdmin or psql

-- Check if users table exists
SELECT 'Checking if users table exists...' AS status;
SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_name = 'users';

-- Check how many users are in the database
SELECT 'Current users in database:' AS status;
SELECT COUNT(*) as total_users FROM users;

-- Show all users (if any)
SELECT 'User details:' AS status;
SELECT id, email, name, role FROM users ORDER BY id;

-- If the table is empty, this will show:
-- total_users = 0

-- If you see "total_users = 0", then run the INSERT statements below:
-- ============================================================
-- FIX: Insert test users if table is empty
-- ============================================================

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

-- Verify the insert worked
SELECT 'FINAL CHECK - Users after insert:' AS status;
SELECT id, email, password, name, role FROM users ORDER BY id;

-- If you see 4 rows above, login will work!
