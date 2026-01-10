package com.enterprise.incident.controller;

import com.enterprise.incident.dto.IncidentDto;
import com.enterprise.incident.entity.Incident;
import com.enterprise.incident.entity.User;
import com.enterprise.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IncidentService incidentService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        log.info("User {} accessing dashboard", user.getUsername());

        IncidentService.IncidentStatistics stats = incidentService.getStatistics(user);
        model.addAttribute("stats", stats);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", user.getRole().equals(User.Role.ROLE_ADMIN));

        return "dashboard";
    }

    @GetMapping("/incidents/my")
    public String myIncidents(@AuthenticationPrincipal User user, Model model) {
        log.info("User {} viewing their incidents", user.getUsername());

        List<Incident> incidents = incidentService.getIncidentsByUser(user);
        model.addAttribute("incidents", incidents);
        model.addAttribute("user", user);

        return "incidents/my-incidents";
    }

    @GetMapping("/incidents/create")
    public String showCreateIncidentForm(Model model) {
        model.addAttribute("incidentDto", new IncidentDto());
        model.addAttribute("severities", Incident.Severity.values());
        return "incidents/create";
    }

    @PostMapping("/incidents/create")
    public String createIncident(
            @Valid @ModelAttribute("incidentDto") IncidentDto dto,
            BindingResult result,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            log.warn("Incident creation validation failed: {}", result.getAllErrors());
            model.addAttribute("severities", Incident.Severity.values());
            return "incidents/create";
        }

        try {
            Incident incident = incidentService.createIncident(dto, user);
            redirectAttributes.addFlashAttribute("message",
                    "Incident created successfully with ID: " + incident.getId());
            log.info("Incident created: ID={}, User={}", incident.getId(), user.getUsername());
            return "redirect:/incidents/my";
        } catch (Exception e) {
            log.error("Error creating incident for user: {}", user.getUsername(), e);
            model.addAttribute("error", "Failed to create incident: " + e.getMessage());
            model.addAttribute("severities", Incident.Severity.values());
            return "incidents/create";
        }
    }

    @GetMapping("/incidents/{id}")
    public String viewIncident(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        try {
            Incident incident = incidentService.getIncidentById(id, user);
            model.addAttribute("incident", incident);
            model.addAttribute("isOwner", incident.getReportedBy().getId().equals(user.getId()));
            model.addAttribute("isAdmin", user.getRole().equals(User.Role.ROLE_ADMIN));
            return "incidents/view";
        } catch (Exception e) {
            log.error("Error viewing incident {}: {}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "error/404";
        }
    }
}

