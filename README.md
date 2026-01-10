# Enterprise Incident Reporting & Tracking System

A production-grade Spring Boot 3.x application for managing security incidents with role-based access control, built for enterprise environments.

## 🏗️ Technology Stack

- **Java**: 21 (LTS)
- **Spring Boot**: 3.2.1
- **Spring Security**: Role-based authentication & authorization
- **Spring Data JPA**: Database persistence
- **Thymeleaf**: Server-side template engine
- **PostgreSQL**: Primary database (MySQL supported)
- **Maven**: Dependency management

## 🔐 Security Features

- ✅ BCrypt password hashing (strength: 12)
- ✅ CSRF protection enabled
- ✅ Session-based authentication (30min timeout)
- ✅ Secure HTTP headers (XSS, CSP, HSTS)
- ✅ Method-level authorization (@PreAuthorize)
- ✅ URL-based security
- ✅ Input validation on all forms
- ✅ No hardcoded credentials
- ✅ Audit logging (SLF4J)

## 👥 User Roles & Permissions

| Action | USER | ADMIN |
|--------|------|-------|
| Register / Login | ✅ | ✅ |
| Create Incident | ✅ | ✅ |
| View own incidents | ✅ | ✅ |
| View all incidents | ❌ | ✅ |
| Update incident | ❌ | ✅ |
| Delete incident | ❌ | ✅ |

## 📁 Project Structure

```
src/main/java/com/enterprise/incident/
├── controller/          # MVC Controllers
│   ├── AuthController.java
│   ├── UserController.java
│   └── AdminController.java
├── service/            # Business Logic
│   ├── UserService.java
│   └── IncidentService.java
├── repository/         # Data Access Layer
│   ├── UserRepository.java
│   └── IncidentRepository.java
├── entity/             # Domain Models
│   ├── User.java
│   └── Incident.java
├── dto/                # Data Transfer Objects
│   ├── RegistrationDto.java
│   └── IncidentDto.java
├── security/           # Security Configuration
├── config/             # Application Configuration
│   └── SecurityConfig.java
├── exception/          # Exception Handling
│   ├── GlobalExceptionHandler.java
│   └── Custom exceptions
└── IncidentApplication.java

src/main/resources/
├── templates/
│   ├── login.html
│   ├── register.html
│   ├── dashboard.html
│   ├── incidents/
│   │   ├── create.html
│   │   ├── my-incidents.html
│   │   └── view.html
│   ├── admin/
│   │   ├── incidents.html
│   │   └── edit-incident.html
│   └── error/
│       ├── 403.html
│       ├── 404.html
│       └── 500.html
└── application.yml
```

## 🚀 Getting Started

### Prerequisites

- JDK 21
- Maven 3.8+
- PostgreSQL 14+ (or MySQL 8+)

### Database Setup

```sql
-- PostgreSQL
CREATE DATABASE incident_db;
CREATE USER incident_user WITH ENCRYPTED PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE incident_db TO incident_user;
```

### Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/incident_db
    username: incident_user
    password: your_secure_password
```

### Build & Run

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/incident-reporting-system-1.0.0.jar
```

Access the application at: `http://localhost:8080`

## 📝 First Time Setup

1. **Register a new user** at `/register`
2. **Login** at `/login`
3. **To create an admin user**, manually update the database:

```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'your_username';
```

## 🔄 CRUD Operations

### Incident Lifecycle

1. **Create**: User reports incident → Status: OPEN
2. **Read**: User views own incidents, Admin views all
3. **Update**: Admin changes status (OPEN → IN_PROGRESS → RESOLVED/REJECTED)
4. **Delete**: Admin permanently removes incidents

### Incident Attributes

- **ID**: Auto-generated
- **Title**: Required (5-200 chars)
- **Description**: Required (10-5000 chars)
- **Severity**: LOW, MEDIUM, HIGH, CRITICAL
- **Status**: OPEN, IN_PROGRESS, RESOLVED, REJECTED
- **Reported By**: Auto-assigned to current user
- **Admin Notes**: Optional notes added by admins
- **Timestamps**: Created At, Updated At

## 🧪 Testing

### Manual Testing Checklist

- [ ] User registration with validation
- [ ] Login with valid/invalid credentials
- [ ] Password requirements enforcement
- [ ] Create incident as USER
- [ ] View own incidents
- [ ] Attempt to access admin panel as USER (should fail)
- [ ] Login as ADMIN
- [ ] View all incidents
- [ ] Update incident status
- [ ] Delete incident
- [ ] CSRF token validation
- [ ] Session timeout
- [ ] Access denied (403) handling

### Security Testing

- [ ] SQL injection attempts
- [ ] XSS attempts in forms
- [ ] CSRF attacks
- [ ] Unauthorized access attempts
- [ ] Password complexity validation
- [ ] Session hijacking prevention

## 🔒 VAPT Readiness

This application is built with security best practices for Vulnerability Assessment and Penetration Testing:

- No sensitive data in HTML/JavaScript
- All passwords BCrypt hashed
- CSRF tokens on all state-changing operations
- Parameterized queries (JPA prevents SQL injection)
- Input validation on all forms
- Secure session management
- HTTP security headers configured
- No stack traces exposed to users
- Comprehensive error handling

## 📊 Logging

Logs are written to:
- Console: All levels
- File: `logs/incident-system.log` (rotating, 30 days)

Logged events include:
- User registration
- Login attempts
- Incident creation
- Status updates
- Deletions
- Security violations

**Note**: Passwords are never logged.

## 🐳 Deployment Notes

### Environment Variables

```bash
export DB_USERNAME=incident_user
export DB_PASSWORD=your_secure_password
export SPRING_PROFILES_ACTIVE=prod
```

### Production Checklist

- [ ] Change default database credentials
- [ ] Set `server.servlet.session.cookie.secure=true` (HTTPS)
- [ ] Configure proper logging (ELK, Splunk)
- [ ] Set up database backups
- [ ] Configure monitoring (Prometheus, Grafana)
- [ ] Use secrets management (Vault, AWS Secrets Manager)
- [ ] Enable HTTPS/TLS
- [ ] Configure firewall rules
- [ ] Set up container orchestration (Kubernetes)
- [ ] Implement CI/CD pipeline

## 📄 License

Enterprise Internal Use Only

## 👨‍💻 Development

Built following enterprise architecture standards:
- Clean layered architecture
- SOLID principles
- DRY principle
- Separation of concerns
- Production-ready error handling
- Comprehensive validation
- Security-first design

---

**Version**: 1.0.0  
**Last Updated**: 2024-01-07  
**Maintained By**: Enterprise Security Team