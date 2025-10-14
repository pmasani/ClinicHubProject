package com.clinichub.visit_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.clinichub.visit_service.config.FeignNoRetryConfig; // Import the configuration class

// Use the standard, single @FeignClient annotation
@FeignClient(
        name = "patientClient",
        url = "${patient.service.url}",
        configuration = FeignNoRetryConfig.class //  Links the client to the custom configuration. This ensures that all requests made through the PatientClient interface will follow the rule of NEVER_RETRY
)
// Specifies a custom configuration class (FeignNoRetryConfig)
// to be used by this specific client. This likely disables
// or modifies the default retry
// mechanism for calls to the patient service.

//@FeignClient(name = "patientClient", url = "${patient.service.url}")

public interface PatientClient {
    @GetMapping("/patients/{id}")
    ResponseEntity<PatientDto> get(@PathVariable String id);
}

// that HTTP call is made exactly once,
// regardless of the success or failure status,
// preventing unintended actions (like duplicate database writes)
// that automatic retries might cause.