package com.enterprise.incident.service;

import com.enterprise.incident.dto.IncidentDto;
import com.enterprise.incident.dto.RegistrationDto;
import com.enterprise.incident.entity.Incident;
import com.enterprise.incident.entity.User;
import com.enterprise.incident.exception.IncidentNotFoundException;
import com.enterprise.incident.exception.UnauthorizedAccessException;
import com.enterprise.incident.repository.IncidentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service handling incident CRUD operations with authorization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentService {

    private final IncidentRepository incidentRepository;

    /**
     * Create a new incident (accessible by USER and ADMIN)
     */
    @Transactional
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Incident createIncident(IncidentDto dto, User reportedBy) {
        log.info("Creating new incident by user: {}", reportedBy.getUsername());

        Incident incident = Incident.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .severity(dto.getSeverity())
                .status(Incident.Status.OPEN)
                .reportedBy(reportedBy)
                .build();

        Incident saved = incidentRepository.save(incident);
        log.info("Incident created successfully with ID: {} by user: {}",
                saved.getId(), reportedBy.getUsername());

        return saved;
    }

    /**
     * Get all incidents reported by a specific user (USER can view own)
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<Incident> getIncidentsByUser(User user) {
        log.info("Fetching incidents for user: {}", user.getUsername());
        return incidentRepository.findByReportedByOrderByCreatedAtDesc(user);
    }

    /**
     * Get all incidents in the system (ADMIN only)
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<Incident> getAllIncidents() {
        log.info("Fetching all incidents (admin access)");
        return incidentRepository.findAllByOrderByCreatedAtDesc(null).getContent();
    }

    /**
     * Get incident by ID with authorization check
     */
    @Transactional(readOnly = true)
    public Incident getIncidentById(Long id, User currentUser) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Incident not found with ID: {}", id);
                    return new IncidentNotFoundException("Incident not found with ID: " + id);
                });

        // Check authorization: USER can only view own incidents, ADMIN can view all
        if (!currentUser.getRole().equals(User.Role.ROLE_ADMIN) &&
                !incident.getReportedBy().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized access attempt by user {} to incident {}",
                    currentUser.getUsername(), id);
            throw new UnauthorizedAccessException("You do not have permission to view this incident");
        }

        return incident;
    }

    /**
     * Update incident status and notes (ADMIN only)
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Incident updateIncident(Long id, Incident.Status status, String adminNotes) {
        log.info("Admin updating incident ID: {} to status: {}", id, status);

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException("Incident not found with ID: " + id));

        incident.setStatus(status);
        if (adminNotes != null && !adminNotes.isBlank()) {
            incident.setAdminNotes(adminNotes);
        }

        Incident updated = incidentRepository.save(incident);
        log.info("Incident ID: {} updated successfully to status: {}", id, status);

        return updated;
    }

    /**
     * Delete incident (ADMIN only)
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteIncident(Long id) {
        log.info("Admin deleting incident ID: {}", id);

        if (!incidentRepository.existsById(id)) {
            throw new IncidentNotFoundException("Incident not found with ID: " + id);
        }

        incidentRepository.deleteById(id);
        log.info("Incident ID: {} deleted successfully", id);
    }

    /**
     * Get incident count statistics
     */
    @Transactional(readOnly = true)
    public IncidentStatistics getStatistics(User user) {
        if (user.getRole().equals(User.Role.ROLE_ADMIN)) {
            return new IncidentStatistics(
                    incidentRepository.count(),
                    incidentRepository.countByStatus(Incident.Status.OPEN),
                    incidentRepository.countByStatus(Incident.Status.IN_PROGRESS),
                    incidentRepository.countByStatus(Incident.Status.RESOLVED),
                    incidentRepository.countBySeverity(Incident.Severity.CRITICAL)
            );
        } else {
            long userTotal = incidentRepository.countByReportedBy(user);
            return new IncidentStatistics(userTotal, 0, 0, 0, 0);
        }
    }

    /**
     * Statistics record
     */
    public record IncidentStatistics(
            long total,
            long open,
            long inProgress,
            long resolved,
            long critical
    ) {}
}