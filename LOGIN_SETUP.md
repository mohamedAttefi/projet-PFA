# Login System Setup Guide

## Quick Start - Database Setup

### Step 1: Create the Database

Open PostgreSQL and run these commands:

```sql
-- Create the database if it doesn't exist
CREATE DATABASE test;

-- Connect to the database
\c test
```

### Step 2: Run the Setup Script

Copy the entire contents of `src/main/resources/setup.sql` and paste it into your PostgreSQL client, or run:

```bash
# Using psql command line
psql -U postgres -d test -f src/main/resources/setup.sql
```

### Step 3: Verify the Setup

In PostgreSQL, verify the tables and users were created:

```sql
-- Check tables exist
\dt

-- Check users are in database
SELECT email, name, role FROM users;

-- Expected output: 4 test users with different roles
```

## Database Credentials

The application uses these credentials (defined in `DBConnection.java`):
- **URL**: `jdbc:postgresql://localhost:5432/test`
- **User**: `postgres`
- **Password**: `ME551234`

**⚠️ Important**: If your PostgreSQL uses different credentials, update them in:
`src/main/java/com/company/workorders/util/DBConnection.java`

## Test Users

After running setup.sql, you can login with these credentials:

| Email | Password | Role | Name |
|-------|----------|------|------|
| `admin@canal-info.fr` | `admin123` | Administrator | Admin Canal |
| `receptionist@canal-info.fr` | `recept123` | Receptionist | Nadia Ben |
| `technician@canal-info.fr` | `tech123` | Technician | Karim Ali |
| `technician2@canal-info.fr` | `tech456` | Technician | Jean Martin |

## Troubleshooting

### Error: "Connexion refusée" / "Identifiants invalides"

**Possible causes:**

1. **Database doesn't exist**
   - Run: `CREATE DATABASE test;`

2. **Tables not created**
   - Run the entire `setup.sql` script

3. **Test users not in database**
   - Check: `SELECT COUNT(*) FROM users;` should return 4
   - If empty, re-run the INSERT statements from setup.sql

4. **Wrong PostgreSQL credentials**
   - Verify `DBConnection.java` has correct URL, USER, and PASSWORD
   - Test connection: `psql -U postgres -h localhost -d test`

5. **PostgreSQL not running**
   - Windows: Start PostgreSQL from Services
   - Mac: `brew services start postgresql`
   - Linux: `sudo systemctl start postgresql`

### Email Not Found in Database

1. Verify user exists:
   ```sql
   SELECT * FROM users WHERE email = 'admin@canal-info.fr';
   ```

2. If empty, insert manually:
   ```sql
   INSERT INTO users (email, password, name, role) 
   VALUES ('admin@canal-info.fr', 'admin123', 'Admin Canal', 'ADMINISTRATOR');
   ```

### Password Not Accepted

The system currently supports both plain-text and BCrypt hashes. Make sure:
- Password matches exactly (case-sensitive)
- No leading/trailing whitespace in database

## Security Note

⚠️ **WARNING**: The test database uses plain-text passwords for convenience. 

**For production**, use BCrypt:
1. Passwords are automatically hashed when users register (future feature)
2. The `AuthService` class already supports BCrypt verification
3. To hash a password manually, use: `AuthService.hashPassword("your_password")`

## Current Login Features

✅ Plain-text and BCrypt password support
✅ Role-based login (RECEPTIONIST, TECHNICIAN, ADMINISTRATOR)
✅ Session persistence via SessionContext
✅ Database connection pooling ready
✅ SQL injection protection via PreparedStatements

## Next Steps

After successful login:
1. Dashboard displays work order statistics
2. Navigate to Clients, Interventions, Users tabs
3. All views are connected to sample data
4. Full CRUD operations can be implemented per view

