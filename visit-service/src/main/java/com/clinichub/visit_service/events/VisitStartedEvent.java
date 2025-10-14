package com.clinichub.visit_service.events;

public record VisitStartedEvent(
        String eventId,
        String patientId,
        String patientName,
        String type,        // VISIT_STARTED
        String at           // ISO-8601 instant
) {}
