package com.clinichub.visit_service.web;

import com.clinichub.visit_service.client.PatientClient;
import com.clinichub.visit_service.client.PatientDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final PatientClient patientClient;

    public VisitController(PatientClient patientClient) {
        this.patientClient = patientClient;
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
        PatientDto p = resp.getBody();
        var event = Map.of(
                "patientId", p.patientId(),
                "patientName", p.firstName() + " " + p.lastName(),
                "type", "VISIT_STARTED",
                "at", Instant.now().toString()
        );
        return ResponseEntity.accepted().body(event);
    }

    @GetMapping("/{patientId}/status")
    public Map<String, Object> status(@PathVariable String patientId) {
        return Map.of("patientId", patientId, "status", "IN_PROGRESS");
    }
}
