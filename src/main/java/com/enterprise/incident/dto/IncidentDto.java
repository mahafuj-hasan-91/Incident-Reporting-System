package com.enterprise.incident.dto;

import com.enterprise.incident.entity.Incident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating new incidents
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentDto {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @NotNull(message = "Severity level is required")
    private Incident.Severity severity;
}

/**
 * DTO for admin to update incident status and notes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class IncidentUpdateDto {

    @NotNull(message = "Status is required")
    private Incident.Status status;

    @Size(max = 5000, message = "Admin notes must not exceed 5000 characters")
    private String adminNotes;
}
