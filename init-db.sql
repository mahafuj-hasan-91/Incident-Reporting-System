-- ============================================
-- Enterprise Incident Reporting System
-- Database Initialization Script
-- PostgreSQL 14+
-- ============================================

-- Create database (run as superuser)
-- CREATE DATABASE incident_db;
-- CREATE USER incident_user WITH ENCRYPTED PASSWORD 'changeme';
-- GRANT ALL PRIVILEGES ON DATABASE incident_db TO incident_user;

-- Connect to incident_db before running the rest

-- ============================================
-- Tables (auto-created by Hibernate, but here for reference)
-- ============================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_email ON users(email);

-- Incidents table
CREATE TABLE IF NOT EXISTS incidents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    admin_notes TEXT,
    reported_by_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (reported_by_id) REFERENCES users(id)
);

CREATE INDEX IF NOT EXISTS idx_status ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_severity ON incidents(severity);
CREATE INDEX IF NOT EXISTS idx_reported_by ON incidents(reported_by_id);
CREATE INDEX IF NOT EXISTS idx_created_at ON incidents(created_at);

-- ============================================
-- Sample Data (Optional - for testing)
-- ============================================

-- Insert sample users (passwords are BCrypt hashed)
-- Password for all users: Password123!
-- Generated with BCrypt strength 12

-- Regular User
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
VALUES (
    'john.doe',
    'john.doe@enterprise.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5PkJ8VaQdHvwq',
    'ROLE_USER',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Admin User
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
VALUES (
    'admin',
    'admin@enterprise.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5PkJ8VaQdHvwq',
    'ROLE_ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Another Regular User
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
VALUES (
    'jane.smith',
    'jane.smith@enterprise.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5PkJ8VaQdHvwq',
    'ROLE_USER',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Insert sample incidents
INSERT INTO incidents (title, description, severity, status, reported_by_id, created_at, updated_at)
VALUES
(
    'Unauthorized Access Attempt Detected',
    'Multiple failed login attempts detected from IP 192.168.1.100. The system blocked the IP after 5 failed attempts. Investigation needed to determine if this is a brute force attack.',
    'HIGH',
    'OPEN',
    (SELECT id FROM users WHERE username = 'john.doe'),
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
),
(
    'Suspicious Email Received',
    'Received a phishing email claiming to be from IT department requesting password reset. Email contained suspicious links. Forwarded to security team for analysis.',
    'MEDIUM',
    'IN_PROGRESS',
    (SELECT id FROM users WHERE username = 'jane.smith'),
    CURRENT_TIMESTAMP - INTERVAL '5 hours',
    CURRENT_TIMESTAMP - INTERVAL '1 hour'
),
(
    'Data Exfiltration Alert',
    'Large volume of data transfer detected from database server to external IP during off-hours. Potential data breach. Immediate investigation required.',
    'CRITICAL',
    'OPEN',
    (SELECT id FROM users WHERE username = 'john.doe'),
    CURRENT_TIMESTAMP - INTERVAL '30 minutes',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
),
(
    'Malware Detection on Workstation',
    'Antivirus detected and quarantined malware on workstation WS-1234. User reported slow performance before detection. System has been isolated from network.',
    'HIGH',
    'RESOLVED',
    (SELECT id FROM users WHERE username = 'jane.smith'),
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '12 hours'
),
(
    'Outdated Software Alert',
    'Critical security patches available for production servers. Several systems running outdated versions with known vulnerabilities.',
    'MEDIUM',
    'OPEN',
    (SELECT id FROM users WHERE username = 'john.doe'),
    CURRENT_TIMESTAMP - INTERVAL '6 hours',
    CURRENT_TIMESTAMP - INTERVAL '6 hours'
);

-- Update some incidents with admin notes
UPDATE incidents
SET admin_notes = 'Investigation in progress. Contacted network team to analyze logs. Temporary firewall rules implemented.',
    updated_at = CURRENT_TIMESTAMP
WHERE title = 'Suspicious Email Received';

UPDATE incidents
SET admin_notes = 'Malware removed successfully. System restored from clean backup. User trained on phishing awareness.',
    updated_at = CURRENT_TIMESTAMP
WHERE title = 'Malware Detection on Workstation';

-- ============================================
-- Verification Queries
-- ============================================

-- Count users by role
SELECT role, COUNT(*) as user_count
FROM users
GROUP BY role;

-- Count incidents by status
SELECT status, COUNT(*) as incident_count
FROM incidents
GROUP BY status;

-- Count incidents by severity
SELECT severity, COUNT(*) as incident_count
FROM incidents
GROUP BY severity;

-- Show all incidents with reporter info
SELECT
    i.id,
    i.title,
    i.severity,
    i.status,
    u.username as reported_by,
    i.created_at
FROM incidents i
JOIN users u ON i.reported_by_id = u.id
ORDER BY i.created_at DESC;

-- ============================================
-- Useful Maintenance Queries
-- ============================================

-- Promote user to admin
-- UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'john.doe';

-- Reset user password (to Password123!)
-- UPDATE users SET password = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5PkJ8VaQdHvwq' WHERE username = 'john.doe';

-- Disable user account
-- UPDATE users SET enabled = FALSE WHERE username = 'john.doe';

-- Delete all incidents (cascade safe)
-- DELETE FROM incidents;

-- Delete specific user and their incidents
-- DELETE FROM incidents WHERE reported_by_id = (SELECT id FROM users WHERE username = 'john.doe');
-- DELETE FROM users WHERE username = 'john.doe';

-- ============================================
-- Cleanup (if needed)
-- ============================================

-- DROP TABLE IF EXISTS incidents CASCADE;
-- DROP TABLE IF EXISTS users CASCADE;