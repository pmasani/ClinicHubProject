package com.clinichub.patient_service.kafka;

import com.clinichub.patient_service.model.Patient;
import com.clinichub.patient_service.model.PatientEvent;
import com.clinichub.patient_service.repo.PatientRepository;
import com.clinichub.patient_service.repo.PatientEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VisitEventConsumer {

    private final PatientRepository patientRepo;
    private final PatientEventRepository eventRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public VisitEventConsumer(PatientRepository patientRepo, PatientEventRepository eventRepo) {
        this.patientRepo = patientRepo;
        this.eventRepo = eventRepo;
    }

    @KafkaListener(topics = "${clinic.kafka.topic}", groupId = "patient-service")
    public void onVisitStarted(Map<String, Object> msg) {
        try {
            String eventId   = (String) msg.get("id");         // or "eventId" depending on your producer
            String patientId = (String) msg.get("patientId");
            String type      = (String) msg.get("type");

            // idempotency for events:
            if (eventId != null && eventRepo.existsById(eventId)) return;

            // store event
            PatientEvent pe = new PatientEvent();
            pe.setEventId(eventId);
            pe.setPatientId(patientId);
            pe.setType(type);
            pe.setPayload(mapper.writeValueAsString(msg));
            eventRepo.save(pe);

            // update patient status
            if (patientId != null) {
                patientRepo.findById(patientId).ifPresent(p -> {
                    if ("VISIT_STARTED".equalsIgnoreCase(type)) p.setStatus("IN_PROGRESS");
                    patientRepo.save(p);
                });
            }
        } catch (Exception e) {
            System.err.println("Failed to process visit event: " + e.getMessage());
        }
    }
}
