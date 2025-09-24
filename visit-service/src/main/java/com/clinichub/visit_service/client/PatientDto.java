package com.clinichub.visit_service.client;

public record PatientDto(
        String patientId,
        String firstName,
        String lastName,
        String dob,
        String phone,
        String status
) {}
