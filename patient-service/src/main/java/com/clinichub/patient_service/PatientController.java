package com.clinichub.patient_service;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST CRUD API:
 * POST   /patients
 * GET    /patients
 * GET    /patients/{id}
 * PUT    /patients/{id}
 * DELETE /patients/{id}
 */
@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientStore store;

    public PatientController(PatientStore store) {
        this.store = store;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PatientDto dto) {
        PatientDto saved = store.create(dto);
        return ResponseEntity.created(URI.create("/patients/" + saved.patientId()))
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        return store.get(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(store.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody PatientDto dto) {
        return store.update(id, dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return store.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
