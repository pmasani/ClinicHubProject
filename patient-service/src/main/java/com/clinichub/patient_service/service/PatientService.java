package com.clinichub.patient_service.service;

import com.clinichub.patient_service.PatientDto;
import com.clinichub.patient_service.model.Patient;
import com.clinichub.patient_service.model.PatientEvent;

import com.clinichub.patient_service.repo.PatientRepository;
import org.springframework.stereotype.Service;
import java.util.stream.StreamSupport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository repo;

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public List<PatientDto> upsertAll(List<PatientDto> batch) {
        // 1. generate IDs if missing, map to entities
        var entities = batch.stream().map(dto -> {
            var p = toEntity(dto);
            if (p.getPatientId() == null || p.getPatientId().isBlank()) {
                p.setPatientId(java.util.UUID.randomUUID().toString());
            }
            return p;
        }).toList();

        // 2. Spring Data Spanner bulk upsert

        var saved = repo.saveAll(entities);

        // 3. Map back to DTO and return
        return StreamSupport.stream(saved.spliterator(), false)
                .map(this::toDto)
                .toList();
    }

    // Retrieves all Patient entities from the database and converts them into a list of PatientDto objects
    public List<PatientDto> list() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(this::toDto)
                .toList();
    }

    public Optional<PatientDto> get(String id) {
        return repo.findById(id).map(this::toDto);
    }

    public PatientDto create(PatientDto dto) {
        Patient p = toEntity(dto);
        if (p.getPatientId() == null || p.getPatientId().isBlank()) {
            p.setPatientId(java.util.UUID.randomUUID().toString());
        }
        // do NOT set p.setUpdatedAt(...); Spanner will fill it
        return toDto(repo.save(p));
    }

    public Optional<PatientDto> update(String id, PatientDto dto) {
        return repo.findById(id).map(existing -> {
            existing.setFirstName(dto.firstName());
            existing.setLastName(dto.lastName());
            existing.setDob(dto.dob());
            existing.setPhone(dto.phone());
            existing.setStatus(dto.status());
            return toDto(repo.save(existing));
        });
    }

    public boolean delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // mapping helpers
    private PatientDto toDto(Patient p) {
        return new PatientDto(
                p.getPatientId(), p.getFirstName(), p.getLastName(),
                p.getDob(), p.getPhone(), p.getStatus());
    }

    private Patient toEntity(PatientDto dto) {
        Patient p = new Patient();
        p.setPatientId(dto.patientId());
        p.setFirstName(dto.firstName());
        p.setLastName(dto.lastName());
        p.setDob(dto.dob());
        p.setPhone(dto.phone());
        p.setStatus(dto.status());
        return p;
    }
}
