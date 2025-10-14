// src/main/java/com/clinichub/visit_service/web/VisitController.java
package com.clinichub.visit_service.web;

import com.clinichub.visit_service.client.PatientClient;
import com.clinichub.visit_service.client.PatientDto;
import com.clinichub.visit_service.events.VisitEventProducer;
import com.clinichub.visit_service.events.VisitStartedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final PatientClient patientClient;
    private final VisitEventProducer producer;

    @Value("${clinic.kafka.topic}")
    private String topic;

    public VisitController(PatientClient patientClient, VisitEventProducer producer) {
        this.patientClient = patientClient;
        this.producer = producer;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestParam String patientId) {
        var resp = patientClient.get(patientId);
        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "PATIENT_NOT_FOUND",
                    "patientId", patientId
            ));
        }

        PatientDto p = resp.getBody(); // assuming record-like accessors
        var evt = new VisitStartedEvent(
                UUID.randomUUID().toString(),
                p.patientId(),
                p.firstName() + " " + p.lastName(),
                "VISIT_STARTED",
                Instant.now().toString()
        );

        producer.publish(topic, evt);

        // Respond to caller with same info
        return ResponseEntity.accepted().body(Map.of(
                "patientId", evt.patientId(),
                "patientName", evt.patientName(),
                "type", evt.type(),
                "at", evt.at().toString()
        ));
    }

    @GetMapping("/{patientId}/status")
    public Map<String, Object> status(@PathVariable String patientId) {
        return Map.of("patientId", patientId, "status", "IN_PROGRESS");
    }
}
