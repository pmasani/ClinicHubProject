package com.clinichub.patient_service;

import com.clinichub.patient_service.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PatientDto> create(@Valid @RequestBody PatientDto dto) {
        var saved = service.create(dto);
        return ResponseEntity.created(URI.create("/patients/" + saved.patientId()))
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> get(@PathVariable String id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<PatientDto> list() {
        return service.list();  // returns seeded rows from Spanner
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> update(@PathVariable String id,
                                             @Valid @RequestBody PatientDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
