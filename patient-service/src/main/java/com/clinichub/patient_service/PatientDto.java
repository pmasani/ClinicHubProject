package com.clinichub.patient_service;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

/**
 * Immutable DTO for requests/responses.
 * Valid statuses: SCHEDULED | SEATED | DONE
 */
public record PatientDto(
        String patientId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate dob,
        @Pattern(regexp = "^[0-9\\-+() ]{7,20}$") String phone,
        @NotBlank String status
) {}
