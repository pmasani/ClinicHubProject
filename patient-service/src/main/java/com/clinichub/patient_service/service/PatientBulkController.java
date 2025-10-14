package com.clinichub.patient_service.service;

import com.clinichub.patient_service.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.clinichub.patient_service.PatientDto;
import java.util.List;

@RestController
@RequestMapping("/patients/bulk")
public class PatientBulkController {
    private final PatientService service;
    public PatientBulkController(PatientService service) { this.service = service; }

    @PostMapping("/upsert")
    public ResponseEntity<List<PatientDto>> upsert(@RequestBody List<PatientDto> batch) {
        return ResponseEntity.ok(service.upsertAll(batch));
    }
}
