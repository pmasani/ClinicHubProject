//package com.clinichub.patient_service;
//
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * Simple in-memory store. We'll swap this for Spanner later.
// */
//@Component
//public class PatientStore {
//
//    private final Map<String, PatientDto> db = new ConcurrentHashMap<>();
//
//    public PatientDto create(PatientDto dto) {
//        String id = Optional.ofNullable(dto.patientId()).orElse(UUID.randomUUID().toString());
//        PatientDto saved = new PatientDto(
//                id, dto.firstName(), dto.lastName(), dto.dob(), dto.phone(), dto.status()
//        );
//        db.put(id, saved);
//        return saved;
//    }
//
//
//    public Optional<PatientDto> get(String id) {
//        return Optional.ofNullable(db.get(id));
//    }
//
//    public List<PatientDto> list() {
//        return new ArrayList<>(db.values());
//    }
//
//    public Optional<PatientDto> update(String id, PatientDto dto) {
//        if (!db.containsKey(id)) return Optional.empty();
//        PatientDto updated = new PatientDto(
//                id, dto.firstName(), dto.lastName(), dto.dob(), dto.phone(), dto.status()
//        );
//        db.put(id, updated);
//        return Optional.of(updated);
//    }
//
//    public boolean delete(String id) {
//        return db.remove(id) != null;
//    }
//}
