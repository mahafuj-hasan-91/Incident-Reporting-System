package com.enterprise.incident.repository;

import com.enterprise.incident.entity.Incident;
import com.enterprise.incident.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Incident entity operations
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Find all incidents reported by a specific user
     */
    List<Incident> findByReportedByOrderByCreatedAtDesc(User user);

    /**
     * Find all incidents with pagination, ordered by creation date
     */
    Page<Incident> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find incidents by status
     */
    List<Incident> findByStatusOrderByCreatedAtDesc(Incident.Status status);

    /**
     * Find incidents by severity
     */
    List<Incident> findBySeverityOrderByCreatedAtDesc(Incident.Severity severity);

    /**
     * Count incidents by status for dashboard
     */
    long countByStatus(Incident.Status status);

    /**
     * Count incidents by severity
     */
    long countBySeverity(Incident.Severity severity);

    /**
     * Count incidents reported by a specific user
     */
    long countByReportedBy(User user);
}
