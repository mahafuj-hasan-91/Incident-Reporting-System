package com.enterprise.incident.controller;

import com.enterprise.incident.dto.RegistrationDto;
import com.enterprise.incident.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller handling authentication: login and registration
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Home page redirect to login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
            log.warn("Failed login attempt");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
            log.info("User logged out");
        }
        return "login";
    }

    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    /**
     * Process user registration
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registrationDto") RegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Check for validation errors
        if (result.hasErrors()) {
            log.warn("Registration validation failed: {}", result.getAllErrors());
            return "register";
        }

        // Check if passwords match
        if (!dto.isPasswordsMatch()) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }

        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("message",
                    "Registration successful! Please log in.");
            log.info("New user registered successfully: {}", dto.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Registration failed for user: {}", dto.getUsername(), e);
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}