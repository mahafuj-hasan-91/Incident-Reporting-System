package com.enterprise.incident.controller;

import com.enterprise.incident.entity.Incident;
import com.enterprise.incident.entity.User;
import com.enterprise.incident.service.IncidentService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for admin-only operations
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final IncidentService incidentService;

    /**
     * View all incidents (admin only)
     */
    @GetMapping("/incidents")
    public String viewAllIncidents(@AuthenticationPrincipal User admin, Model model) {
        log.info("Admin {} viewing all incidents", admin.getUsername());

        List<Incident> incidents = incidentService.getAllIncidents();
        model.addAttribute("incidents", incidents);
        model.addAttribute("statuses", Incident.Status.values());
        model.addAttribute("severities", Incident.Severity.values());

        return "admin/incidents";
    }

    /**
     * Show edit incident form
     */
    @GetMapping("/incidents/edit/{id}")
    public String showEditIncidentForm(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin,
            Model model
    ) {
        try {
            Incident incident = incidentService.getIncidentById(id, admin);
            model.addAttribute("incident", incident);
            model.addAttribute("statuses", Incident.Status.values());
            return "admin/edit-incident";
        } catch (Exception e) {
            log.error("Admin {} error accessing incident {}: {}",
                    admin.getUsername(), id, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }

    /**
     * Update incident status and notes
     */
    @PostMapping("/incidents/update/{id}")
    public String updateIncident(
            @PathVariable Long id,
            @RequestParam("status") Incident.Status status,
            @RequestParam(value = "adminNotes", required = false) String adminNotes,
            @AuthenticationPrincipal User admin,
            RedirectAttributes redirectAttributes
    ) {
        try {
            incidentService.updateIncident(id, status, adminNotes);
            redirectAttributes.addFlashAttribute("message",
                    "Incident updated successfully");
            log.info("Admin {} updated incident {}", admin.getUsername(), id);
            return "redirect:/admin/incidents";
        } catch (Exception e) {
            log.error("Admin {} error updating incident {}: {}",
                    admin.getUsername(), id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update incident: " + e.getMessage());
            return "redirect:/admin/incidents/edit/" + id;
        }
    }

    /**
     * Delete incident
     */
    @PostMapping("/incidents/delete/{id}")
    public String deleteIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User admin,
            RedirectAttributes redirectAttributes
    ) {
        try {
            incidentService.deleteIncident(id);
            redirectAttributes.addFlashAttribute("message",
                    "Incident deleted successfully");
            log.info("Admin {} deleted incident {}", admin.getUsername(), id);
        } catch (Exception e) {
            log.error("Admin {} error deleting incident {}: {}",
                    admin.getUsername(), id, e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Failed to delete incident: " + e.getMessage());
        }
        return "redirect:/admin/incidents";
    }
}