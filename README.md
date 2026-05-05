# Canal Informatique - Gestion des interventions

Modern desktop application for managing work orders and IT maintenance operations.

## Quick Start (5 Minutes)

### 1️⃣ Database Setup

**⚠️ If login shows "Identifiants invalides", do this first:**

Open **pgAdmin** or **psql**, select the `test` database, and paste the entire content of:
```
📄 QUICK_SETUP.sql
```

Or run from command line:
```bash
psql -U postgres -d test -f QUICK_SETUP.sql
```

**After running, you should see:**
```
SUCCESS! 4 users ready to login
```

### 2️⃣ Start the Application

```bash
mvn clean javafx:run
```

### 3️⃣ Login with Test Credentials

Use any of these accounts:

| Email | Password |
|-------|----------|
| `admin@canal-info.fr` | `admin123` |
| `receptionist@canal-info.fr` | `recept123` |
| `technician@canal-info.fr` | `tech123` |
| `technician2@canal-info.fr` | `tech456` |

## Project Structure

```
workorders/
├── src/main/
│   ├── java/com/company/
│   │   ├── Main.java                 # Application entry point
│   │   ├── workorders/
│   │   │   ├── controller/          # View controllers (Dashboard, Clients, etc.)
│   │   │   ├── model/               # Domain objects (User, UserRole)
│   │   │   ├── service/             # Business logic (AuthService, SessionContext)
│   │   │   └── util/                # Utilities (AppNavigator, LoginController)
│   ├── resources/
│   │   ├── main_view.fxml           # Login screen
│   │   ├── setup.sql                # Full database schema
│   │   ├── views/                   # Application screens
│   │   │   ├── app-shell.fxml       # Main navigation shell
│   │   │   ├── dashboard-view.fxml  # Dashboard/statistics
│   │   │   ├── clients-view.fxml    # Client management
│   │   │   ├── interventions-view.fxml
│   │   │   ├── users-view.fxml
│   │   │   ├── notifications-view.fxml
│   │   │   └── history-view.fxml
├── pom.xml                           # Maven configuration
├── LOGIN_SETUP.md                    # Detailed login/database guide
└── QUICK_SETUP.sql                   # 1-minute database setup
```

## Features

### ✅ Currently Implemented

- **Secure Authentication**
  - Email/password login with encrypted session storage
  - Role-based access (Admin, Receptionist, Technician)
  - SQLi protection via PreparedStatements

- **Multi-view Dashboard**
  - Work order statistics and KPIs
  - Recent activity feed
  - Priority alerts
  - Navigation sidebar

- **Main Modules** (ready for CRUD operations)
  - Dashboard: Statistics and monitoring
  - Clients: Client database management
  - Interventions: Work order tracking
  - Users: User account management
  - Notifications: Real-time alerts
  - History: Audit trail of actions

### 🔲 Coming Soon

- [ ] Full CRUD operations for clients and interventions
- [ ] Database persistence for all forms
- [ ] Advanced filtering and search
- [ ] Export reports (PDF, Excel)
- [ ] Email notifications
- [ ] Two-factor authentication
- [ ] Dark mode

## Architecture

**Layered Design:**

```
┌─────────────────────────────────────────┐
│  Presentation Layer (JavaFX Views)      │
│  *.fxml files + Controllers             │
├─────────────────────────────────────────┤
│  Application Layer (Services)           │
│  AuthService, SessionContext, etc.      │
├─────────────────────────────────────────┤
│  Data Access Layer (DAO/JDBC)           │
│  DBConnection, repository classes       │
├─────────────────────────────────────────┤
│  PostgreSQL Database                    │
│  users, clients, interventions tables   │
└─────────────────────────────────────────┘
```

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 17 | Core language |
| JavaFX | 21 | Desktop UI framework |
| PostgreSQL | 12+ | Relational database |
| Maven | 3.9+ | Build tool |
| BCrypt | 0.4 | Password hashing |
| JDBC | Native | Database driver |

## Database Schema

### users
```sql
id (PK) | email | password | name | role | created_at
```

### clients
```sql
id (PK) | company_name | phone | address | email | created_at
```

### interventions
```sql
id (PK) | title | description | priority | status | client_id (FK) | assigned_to (FK) | created_at
```

### notifications
```sql
id (PK) | user_id (FK) | message | is_read | created_at
```

### history
```sql
id (PK) | user_id (FK) | action | entity_type | entity_id | details | created_at
```

## Configuration

### Database Connection

**File:** `src/main/java/com/company/workorders/util/DBConnection.java`

```java
private static final String URL = "jdbc:postgresql://localhost:5432/test";
private static final String USER = "postgres";
private static final String PASSWORD = "ME551234";  // Change this!
```

**To use different credentials:**

1. Update the constants above
2. Ensure the `test` database exists with proper tables
3. Run `QUICK_SETUP.sql` with the new database

## Troubleshooting

### Login fails with "Identifiants invalides"

**Solution:**
1. Run `QUICK_SETUP.sql` in your PostgreSQL client
2. Verify users exist: `SELECT COUNT(*) FROM users;`
3. Check password exactly matches (case-sensitive)

### "Unable to connect to database"

**Solution:**
1. Ensure PostgreSQL is running: `psql -U postgres`
2. Verify `test` database exists: `\l`
3. Check credentials in `DBConnection.java`
4. Verify firewall allows port 5432

### Application won't start

**Solution:**
```bash
# Clean and rebuild
mvn clean install

# Check for compilation errors
mvn compile
```

## Development

### Build the project

```bash
mvn clean package
```

### Run tests

```bash
mvn test
```

### Run with debug logging

```bash
mvn clean javafx:run -X
```

## Security Notes

⚠️ **For Development ONLY**

- Passwords are stored plain-text in test database
- No HTTPS/TLS configured
- No rate limiting on login attempts

✅ **Production Checklist**

- [ ] Enable BCrypt password hashing
- [ ] Implement password reset functionality
- [ ] Add login attempt rate limiting
- [ ] Configure HTTPS/TLS for connections
- [ ] Use environment variables for DB credentials
- [ ] Implement two-factor authentication
- [ ] Add session timeout
- [ ] Enable audit logging

## API Reference

### AuthService

```java
// Authenticate user
AuthResult result = authService.authenticate(email, password);

// Hash password (BCrypt)
String hash = AuthService.hashPassword(plainPassword);
```

### SessionContext

```java
// Get current user
User currentUser = SessionContext.getCurrentUser();

// Logout
SessionContext.clear();
```

### AppNavigator

```java
// Navigate between screens
AppNavigator.showAppShell();
AppNavigator.showLogin();
AppNavigator.loadView("/views/clients-view.fxml");
```

## Support & Documentation

- 📖 **Setup Guide:** [LOGIN_SETUP.md](LOGIN_SETUP.md)
- 📄 **Database Schema:** [setup.sql](src/main/resources/setup.sql)
- ⚡ **Quick Fix:** [QUICK_SETUP.sql](QUICK_SETUP.sql)

## License

Copyright © 2026 Canal Informatique. All rights reserved.

## Contributors

- Development Team @ Canal Informatique
