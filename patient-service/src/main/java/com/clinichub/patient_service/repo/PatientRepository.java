package com.clinichub.patient_service.repo;

import com.clinichub.patient_service.model.Patient;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends SpannerRepository<Patient, String> {}
