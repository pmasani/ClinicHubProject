package com.clinichub.visit_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patientClient", url = "${patient.service.url}")
public interface PatientClient {
    @GetMapping("/patients/{id}")
    ResponseEntity<PatientDto> get(@PathVariable String id);
}
