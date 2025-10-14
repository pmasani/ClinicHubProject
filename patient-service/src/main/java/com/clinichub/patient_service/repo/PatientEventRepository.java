package com.clinichub.patient_service.repo;

import com.clinichub.patient_service.model.PatientEvent;
import com.google.cloud.spring.data.spanner.repository.SpannerRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientEventRepository extends SpannerRepository<PatientEvent, String> {}
