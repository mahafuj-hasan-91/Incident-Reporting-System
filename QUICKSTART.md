# Quick Start Guide

Get the Incident Reporting System running in 5 minutes.

## Prerequisites Check

```bash
# Verify Java 21
java -version
# Should show: openjdk version "21.x.x"

# Verify Maven
mvn -version
# Should show: Apache Maven 3.8+

# Verify PostgreSQL
psql --version
# Should show: psql (PostgreSQL) 14+
```

## Step 1: Database Setup (2 minutes)

```bash
# Start PostgreSQL (if not running)
# Linux/Mac
sudo systemctl start postgresql

# Or using Docker
docker run --name incident-postgres \
  -e POSTGRES_PASSWORD=changeme \
  -e POSTGRES_DB=incident_db \
  -e POSTGRES_USER=incident_user \
  -p 5432:5432 \
  -d postgres:15

# Create database manually (if not using Docker)
psql -U postgres
```

```sql
CREATE DATABASE incident_db;
CREATE USER incident_user WITH ENCRYPTED PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE incident_db TO incident_user;
\q
```

## Step 2: Configure Application (30 seconds)

The default `application.yml` is already configured for localhost. If needed, update:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/incident_db
    username: incident_user
    password: changeme
```

## Step 3: Build & Run (2 minutes)

```bash
# Clone/navigate to project directory
cd incident-reporting-system

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

Wait for:
```
Started IncidentApplication in X.XXX seconds
```

## Step 4: Access & Test (30 seconds)

Open browser: `http://localhost:8080`

### Test Credentials (if using init-db.sql):

**Regular User:**
- Username: `john.doe`
- Password: `Password123!`

**Admin User:**
- Username: `admin`
- Password: `Password123!`

### Or Register New User:

1. Click "Register here"
2. Fill form with:
    - Username: `testuser`
    - Email: `test@enterprise.com`
    - Password: `SecurePass123!` (must meet requirements)
    - Confirm Password: `SecurePass123!`
3. Click "Create Account"
4. Login with new credentials

## Step 5: Create Your First Incident

1. After login → Dashboard
2. Click "Report New Incident"
3. Fill form:
    - Title: `Test Security Incident`
    - Severity: `HIGH`
    - Description: `This is a test incident to verify system functionality`
4. Click "Submit Incident"
5. View in "My Incidents"

## Step 6: Test Admin Features (Optional)

To make your user an admin:

```bash
# Connect to database
psql -U incident_user -d incident_db

# Promote user to admin
UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'testuser';
\q
```

Logout and login again. You'll now see "Admin Panel" in navigation.

## Common Issues & Solutions

### Issue: Port 8080 already in use
```bash
# Find process using port
lsof -i :8080
# Kill it or change port in application.yml:
# server.port: 8081
```

### Issue: Database connection failed
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Check credentials in application.yml match database
# Check database exists:
psql -U incident_user -d incident_db -c "SELECT 1;"
```

### Issue: Build fails
```bash
# Clean Maven cache
mvn clean
rm -rf ~/.m2/repository/com/enterprise

# Rebuild
mvn clean install -U
```

### Issue: JDK version mismatch
```bash
# Check JAVA_HOME
echo $JAVA_HOME

# Should point to JDK 21
# Set if needed:
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

## Production Deployment Quick Checklist

Before deploying to production:

- [ ] Change database password
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (not `update`)
- [ ] Enable HTTPS: `server.servlet.session.cookie.secure=true`
- [ ] Set `spring.profiles.active=prod`
- [ ] Configure external logging
- [ ] Set up database backups
- [ ] Review and adjust session timeout
- [ ] Enable monitoring/metrics
- [ ] Run security scan (OWASP ZAP)
- [ ] Load test the application
- [ ] Set up error alerting

## API Testing with cURL (Optional)

```bash
# Get CSRF token (required for POST)
curl -c cookies.txt http://localhost:8080/login

# Login
curl -b cookies.txt -c cookies.txt \
  -X POST http://localhost:8080/login \
  -d "username=admin&password=Password123!" \
  -d "_csrf=$(grep XSRF-TOKEN cookies.txt | awk '{print $7}')"

# Access protected resource
curl -b cookies.txt http://localhost:8080/dashboard
```

## Next Steps

1. Read full [README.md](README.md) for architecture details
2. Review security configuration in `SecurityConfig.java`
3. Customize incident fields in `Incident.java` entity
4. Add email notifications (see TODO in services)
5. Integrate with your SSO/LDAP
6. Set up CI/CD pipeline
7. Configure production monitoring

## Getting Help

- Check logs: `logs/incident-system.log`
- Enable debug: `logging.level.com.enterprise.incident=DEBUG`
- Review Spring Security debug: `logging.level.org.springframework.security=DEBUG`

## Default URLs

- Login: `http://localhost:8080/login`
- Register: `http://localhost:8080/register`
- Dashboard: `http://localhost:8080/dashboard`
- My Incidents: `http://localhost:8080/incidents/my`
- Create Incident: `http://localhost:8080/incidents/create`
- Admin Panel: `http://localhost:8080/admin/incidents` (admin only)

---

**You're ready!** The system is now running and ready for incident reporting. 🚀