# WorkOrders Management System - Distribution Package

## 🚀 Quick Start

### Requirements
- Java 17 or higher
- PostgreSQL database (optional - see database setup below)

### Running the Application

#### Option 1: Executable JAR (Recommended)
1. Double-click `workorders-1.0-SNAPSHOT-executable.jar`
2. Or run from command line:
   ```bash
   java -jar workorders-1.0-SNAPSHOT-executable.jar
   ```

#### Option 2: Using Maven
```bash
mvn javafx:run
```

## 📊 Features

### ✅ Real-Time Dashboard
- Live statistics from database
- Dynamic intervention tracking
- Performance metrics and SLA monitoring
- Real-time workload distribution

### 🔔 Smart Notifications
- Automatic notifications for intervention events
- Status change alerts
- Assignment notifications
- Urgent intervention alerts
- System-wide and user-specific notifications

### 📋 Intervention Management
- Create, update, and track interventions
- Dynamic status timeline showing real progress
- Client and technician assignment
- Priority-based workflow
- Comments and history tracking

### 👥 User Management
- Role-based access (Admin, Technician, Receptionist)
- Secure authentication with password hashing
- User activity tracking

## 🗄️ Database Setup

### PostgreSQL Configuration
The application will automatically create and update database schema. Ensure PostgreSQL is running and accessible.

Default connection settings can be configured in `DBConnection.java`.

### Automatic Schema Updates
The application includes automatic database migration:
- Adds missing columns automatically
- Ensures compatibility with existing data
- Updates schema on application startup

## 📁 Distribution Package

### Included Files
- `workorders-1.0-SNAPSHOT-executable.jar` - Main application (all dependencies included)
- `start.bat` - Windows startup script
- `start.sh` - Linux/Mac startup script
- `README-DISTRIBUTION.md` - This documentation

### Installation
1. Extract the distribution package
2. Ensure Java 17+ is installed
3. Run the application using your preferred method

## 🔧 Development

### Build from Source
```bash
# Compile
mvn clean compile

# Create executable JAR
mvn clean package

# Run application
mvn javafx:run
```

### Project Structure
```
src/main/java/com/company/workorders/
├── controller/     # UI controllers
├── dao/           # Data access objects
├── model/         # Data models
├── service/       # Business logic
└── util/          # Utilities and helpers
```

## 📞 Support

For technical support or questions:
- Check the console output for error messages
- Ensure database connectivity
- Verify Java version compatibility

---

**Version:** 1.0-SNAPSHOT  
**Build Date:** 2026-05-11  
**Requirements:** Java 17+, PostgreSQL
